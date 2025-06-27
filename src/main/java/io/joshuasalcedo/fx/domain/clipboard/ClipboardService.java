package io.joshuasalcedo.fx.domain.clipboard;

import io.joshuasalcedo.clipboard.core.Clipboard;
import io.joshuasalcedo.clipboard.core.ClipboardMonitor;
import io.joshuasalcedo.fx.api.ClipboardWebSocketController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import io.joshuasalcedo.fx.infrastructure.events.ClipboardRunner;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class ClipboardService {
    private static final Logger log = LoggerFactory.getLogger(ClipboardService.class);
    private static final int PREVIEW_LENGTH = 50;
    private static final int MAX_DUPLICATE_CHECK_HOURS = 24;

    private final ClipboardRepository clipboardRepository;
    private final ClipboardMonitor clipboardMonitor;
    private final ApplicationEventPublisher eventPublisher;
    private final ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);
    
    // Optional WebSocket controller for real-time updates
    private ClipboardWebSocketController webSocketController;
    
    // ClipboardRunner is injected after bean creation to avoid circular dependency
    @Autowired
    private ClipboardRunner clipboardRunner;

    public ClipboardService(ClipboardRepository clipboardRepository, 
                          ClipboardMonitor clipboardMonitor,
                          ApplicationEventPublisher eventPublisher) {
        this.clipboardRepository = clipboardRepository;
        this.clipboardMonitor = clipboardMonitor;
        this.eventPublisher = eventPublisher;
        log.info("ClipboardService initialized");
        
        // Schedule periodic cleanup of old entries
        scheduleAutomaticCleanup();
    }
    
    // Setter for optional WebSocket controller
    public void setWebSocketController(ClipboardWebSocketController webSocketController) {
        this.webSocketController = webSocketController;
    }

    @EventListener
    public void handleClipboardSavedEvent(ClipboardSavedEvent event) {
        save(event.getContent());
    }

    /**
     * Saves a new clipboard entry with duplicate detection
     * @param content The clipboard content to save
     * @return The saved clipboard entry
     */
    @Transactional
    public ClipboardEntry save(String content) {
        log.debug("Attempting to save clipboard entry with {} characters", content != null ? content.length() : 0);

        if (content == null || content.trim().isEmpty()) {
            log.warn("Attempted to save null or empty clipboard content");
            throw new IllegalArgumentException("Clipboard content cannot be null or empty");
        }

        try {
            // Check for recent duplicates
            Optional<ClipboardEntry> recentDuplicate = findRecentDuplicate(content);
            if (recentDuplicate.isPresent()) {
                log.info("Duplicate content detected, updating timestamp for entry ID: {}", recentDuplicate.get().getId());
                ClipboardEntry existing = recentDuplicate.get();
                existing.setTimestamp(LocalDateTime.now());
                ClipboardEntry updated = clipboardRepository.save(existing);
                
                // Notify WebSocket clients of update
                notifyWebSocketUpdate(updated);
                
                return updated;
            }

            // Create new entry
            ClipboardEntry clipboardEntry = ClipboardEntry.builder()
                    .content(content)
                    .timestamp(LocalDateTime.now())
                    .build();

            ClipboardEntry saved = clipboardRepository.save(clipboardEntry);

            String preview = truncateForLogging(saved.getContent(), PREVIEW_LENGTH);
            log.info("New clipboard entry saved - ID: {}, Length: {}, Preview: {}",
                    saved.getId(), saved.getContentLength(), preview);

            // Publish event for other components
            eventPublisher.publishEvent(new ClipboardEntryCreatedEvent(saved));
            
            // Notify WebSocket clients
            notifyWebSocketNewEntry(saved);

            return saved;

        } catch (Exception e) {
            log.error("Failed to save clipboard entry: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save clipboard entry", e);
        }
    }

    /**
     * Pins or unpins a clipboard entry
     * @param id The entry ID
     * @param pinned True to pin, false to unpin
     * @return The updated entry, or empty if not found
     */
    @Transactional
    public Optional<ClipboardEntry> setPinned(Long id, boolean pinned) {
        log.debug("Setting pinned={} for clipboard entry ID: {}", pinned, id);

        return clipboardRepository.findById(id)
                .map(entry -> {
                    entry.setPinned(pinned);
                    ClipboardEntry saved = clipboardRepository.save(entry);
                    log.info("Clipboard entry {} {}", id, pinned ? "pinned" : "unpinned");
                    
                    // Notify WebSocket clients
                    notifyWebSocketUpdate(saved);
                    
                    return saved;
                })
                .or(() -> {
                    log.warn("Clipboard entry not found for ID: {}", id);
                    return Optional.empty();
                });
    }

    /**
     * Convenience method to pin an entry
     */
    @Transactional
    public Optional<ClipboardEntry> pin(Long id) {
        return setPinned(id, true);
    }

    /**
     * Convenience method to unpin an entry
     */
    @Transactional
    public Optional<ClipboardEntry> unpin(Long id) {
        return setPinned(id, false);
    }

    /**
     * Deletes all clipboard entries with option to preserve pinned entries
     * @param includePinned If true, deletes pinned entries as well
     * @return The number of deleted entries
     */
    @Transactional
    public long deleteAll(boolean includePinned) {
        log.info("Deleting all clipboard entries (includePinned: {})", includePinned);

        try {
            long countBefore = clipboardRepository.count();

            if (includePinned) {
                clipboardRepository.deleteAll();
                log.info("Deleted all {} clipboard entries", countBefore);
                
                // Notify WebSocket clients
                notifyWebSocketClear(true);
                
                return countBefore;
            } else {
                int deletedCount = clipboardRepository.deleteByIsPinnedFalse();
                log.info("Deleted {} unpinned clipboard entries, {} pinned entries preserved",
                        deletedCount, countBefore - deletedCount);
                
                // Notify WebSocket clients
                notifyWebSocketClear(false);
                
                return deletedCount;
            }
        } catch (Exception e) {
            log.error("Failed to delete clipboard entries: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete clipboard entries", e);
        }
    }

    /**
     * Deletes all unpinned clipboard entries
     */
    @Transactional
    public long deleteAll() {
        return deleteAll(false);
    }

    /**
     * Deletes a specific clipboard entry
     * @param id The entry ID to delete
     * @return true if deleted, false if not found
     */
    @Transactional
    public boolean deleteById(Long id) {
        log.debug("Attempting to delete clipboard entry ID: {}", id);

        if (!clipboardRepository.existsById(id)) {
            log.warn("Cannot delete - clipboard entry not found for ID: {}", id);
            return false;
        }

        clipboardRepository.deleteById(id);
        log.info("Deleted clipboard entry ID: {}", id);
        
        // Notify WebSocket clients
        notifyWebSocketDelete(id);
        
        return true;
    }

    /**
     * Finds a clipboard entry by ID
     */
    @Transactional(readOnly = true)
    public Optional<ClipboardEntry> findById(Long id) {
        log.debug("Finding clipboard entry by ID: {}", id);
        return clipboardRepository.findById(id);
    }

    /**
     * Gets all clipboard entries with pagination
     */
    @Transactional(readOnly = true)
    public Page<ClipboardEntry> findAll(Pageable pageable) {
        log.debug("Finding all clipboard entries - page: {}, size: {}",
                pageable.getPageNumber(), pageable.getPageSize());
        return clipboardRepository.findAllOrderByTimestampDesc(pageable);
    }

    /**
     * Gets all pinned clipboard entries
     */
    @Transactional(readOnly = true)
    public List<ClipboardEntry> findAllPinned() {
        log.debug("Finding all pinned clipboard entries");
        List<ClipboardEntry> pinned = clipboardRepository.findByIsPinnedTrueOrderByTimestampDesc();
        log.debug("Found {} pinned entries", pinned.size());
        return pinned;
    }

    /**
     * Searches clipboard entries by content
     * @param searchTerm The search term
     * @param pageable Pagination information
     * @return Page of matching entries
     */
    @Transactional(readOnly = true)
    public Page<ClipboardEntry> searchByContent(String searchTerm, Pageable pageable) {
        log.debug("Searching clipboard entries for term: '{}' - page: {}, size: {}",
                searchTerm, pageable.getPageNumber(), pageable.getPageSize());

        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            log.warn("Empty search term provided");
            return Page.empty(pageable);
        }

        return clipboardRepository.findByContentContainingIgnoreCase(searchTerm, pageable);
    }

    /**
     * Gets clipboard entries from the last N hours
     * @param hours Number of hours to look back
     * @return List of recent entries
     */
    @Transactional(readOnly = true)
    public List<ClipboardEntry> findRecent(int hours) {
        log.debug("Finding clipboard entries from the last {} hours", hours);
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        List<ClipboardEntry> recent = clipboardRepository.findByTimestampAfterOrderByTimestampDesc(since);
        log.debug("Found {} entries since {}", recent.size(), since);
        return recent;
    }

    /**
     * Deletes clipboard entries older than specified hours
     * @param hours Age threshold in hours
     * @param includePinned Whether to include pinned entries
     * @return Number of deleted entries
     */
    @Transactional
    public long deleteOlderThan(int hours, boolean includePinned) {
        LocalDateTime threshold = LocalDateTime.now().minusHours(hours);
        log.info("Deleting clipboard entries older than {} (includePinned: {})", threshold, includePinned);

        try {
            long deletedCount;
            if (includePinned) {
                deletedCount = clipboardRepository.deleteByTimestampBefore(threshold);
            } else {
                deletedCount = clipboardRepository.deleteByTimestampBeforeAndIsPinnedFalse(threshold);
            }

            log.info("Deleted {} clipboard entries older than {} hours", deletedCount, hours);
            return deletedCount;

        } catch (Exception e) {
            log.error("Failed to delete old clipboard entries: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete old clipboard entries", e);
        }
    }

    /**
     * Gets statistics about clipboard entries
     */
    @Transactional(readOnly = true)
    public ClipboardStats getStats() {
        log.debug("Calculating clipboard statistics");

        long totalCount = clipboardRepository.count();
        long pinnedCount = clipboardRepository.countByisPinnedTrue();
        LocalDateTime oldestEntry = clipboardRepository.findOldestTimestamp();
        LocalDateTime newestEntry = clipboardRepository.findNewestTimestamp();

        ClipboardStats stats = ClipboardStats.builder()
                .totalEntries(totalCount)
                .pinnedEntries(pinnedCount)
                .unpinnedEntries(totalCount - pinnedCount)
                .oldestEntry(oldestEntry)
                .newestEntry(newestEntry)
                .build();

        log.info("Clipboard stats - Total: {}, Pinned: {}, Unpinned: {}",
                totalCount, pinnedCount, totalCount - pinnedCount);

        return stats;
    }

    /**
     * Exports clipboard entries to various formats
     */
    public CompletableFuture<String> exportEntries(ExportFormat format, boolean includePinned) {
        return CompletableFuture.supplyAsync(() -> {
            List<ClipboardEntry> entries = includePinned ? 
                clipboardRepository.findAll() : 
                clipboardRepository.findByIsPinnedFalseOrderByTimestampDesc();
            
            switch (format) {
                case JSON:
                    return exportToJson(entries);
                case CSV:
                    return exportToCsv(entries);
                case TXT:
                    return exportToText(entries);
                default:
                    throw new IllegalArgumentException("Unsupported export format: " + format);
            }
        });
    }

    /**
     * Stops clipboard monitoring
     */
    public boolean stopClipboard() {
        return clipboardRunner.stop();
    }

    /**
     * Starts clipboard monitoring if stopped
     */
    public boolean startClipboard() {
        return clipboardRunner.start();
    }

    /**
     * Checks if clipboard monitoring is active
     */
    public boolean isMonitoringActive() {
        return clipboardRunner.getMonitoringSession() != null && 
               !clipboardRunner.getMonitoringSession().isStopped();
    }

    // Private helper methods

    /**
     * Checks for recent duplicates of the given content
     */
    private Optional<ClipboardEntry> findRecentDuplicate(String content) {
        LocalDateTime since = LocalDateTime.now().minusHours(MAX_DUPLICATE_CHECK_HOURS);
        return clipboardRepository.findFirstByContentAndTimestampAfter(content, since);
    }

    /**
     * Truncates text for logging purposes
     */
    private String truncateForLogging(String text, int maxLength) {
        if (text == null) return "null";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength) + "...";
    }

    /**
     * Schedules automatic cleanup of old entries
     */
    private void scheduleAutomaticCleanup() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                long deleted = deleteOlderThan(7 * 24, false); // Delete unpinned entries older than 7 days
                if (deleted > 0) {
                    log.info("Automatic cleanup deleted {} old entries", deleted);
                }
            } catch (Exception e) {
                log.error("Error during automatic cleanup", e);
            }
        }, 1, 24, TimeUnit.HOURS); // Run daily
    }

    // WebSocket notification methods
    
    private void notifyWebSocketNewEntry(ClipboardEntry entry) {
        if (webSocketController != null) {
            webSocketController.broadcastNewEntry(entry);
        }
    }
    
    private void notifyWebSocketUpdate(ClipboardEntry entry) {
        if (webSocketController != null) {
            webSocketController.broadcastUpdate(entry);
        }
    }
    
    private void notifyWebSocketDelete(Long entryId) {
        if (webSocketController != null) {
            webSocketController.broadcastDelete(entryId);
        }
    }
    
    private void notifyWebSocketClear(boolean includePinned) {
        if (webSocketController != null) {
            webSocketController.broadcastClear(includePinned);
        }
    }

    // Export helper methods
    
    private String exportToJson(List<ClipboardEntry> entries) {
        // Simple JSON export - in production, use Jackson ObjectMapper
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < entries.size(); i++) {
            ClipboardEntry entry = entries.get(i);
            json.append("{")
                .append("\"id\":").append(entry.getId()).append(",")
                .append("\"content\":\"").append(escapeJson(entry.getContent())).append("\",")
                .append("\"timestamp\":\"").append(entry.getTimestamp()).append("\",")
                .append("\"pinned\":").append(entry.isPinned())
                .append("}");
            if (i < entries.size() - 1) json.append(",");
        }
        json.append("]");
        return json.toString();
    }
    
    private String exportToCsv(List<ClipboardEntry> entries) {
        StringBuilder csv = new StringBuilder("ID,Content,Timestamp,Pinned\n");
        for (ClipboardEntry entry : entries) {
            csv.append(entry.getId()).append(",")
               .append("\"").append(escapeCsv(entry.getContent())).append("\",")
               .append(entry.getTimestamp()).append(",")
               .append(entry.isPinned()).append("\n");
        }
        return csv.toString();
    }
    
    private String exportToText(List<ClipboardEntry> entries) {
        StringBuilder text = new StringBuilder();
        for (ClipboardEntry entry : entries) {
            text.append("=== Entry ").append(entry.getId()).append(" ===\n")
                .append("Time: ").append(entry.getTimestamp()).append("\n")
                .append("Pinned: ").append(entry.isPinned()).append("\n")
                .append("Content:\n").append(entry.getContent()).append("\n\n");
        }
        return text.toString();
    }
    
    private String escapeJson(String str) {
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
    
    private String escapeCsv(String str) {
        return str.replace("\"", "\"\"");
    }

    // Enums and inner classes
    
    public enum ExportFormat {
        JSON, CSV, TXT
    }

    /**
     * Event published when a new clipboard entry is created
     */
    public static class ClipboardEntryCreatedEvent {
        private final ClipboardEntry entry;
        
        public ClipboardEntryCreatedEvent(ClipboardEntry entry) {
            this.entry = entry;
        }
        
        public ClipboardEntry getEntry() {
            return entry;
        }
    }

    /**
     * Stats DTO for clipboard entries
     */
    public static class ClipboardStats {
        private long totalEntries;
        private long pinnedEntries;
        private long unpinnedEntries;
        private LocalDateTime oldestEntry;
        private LocalDateTime newestEntry;

        public ClipboardStats() {}

        public ClipboardStats(long totalEntries, long pinnedEntries, long unpinnedEntries, LocalDateTime oldestEntry, LocalDateTime newestEntry) {
            this.totalEntries = totalEntries;
            this.pinnedEntries = pinnedEntries;
            this.unpinnedEntries = unpinnedEntries;
            this.oldestEntry = oldestEntry;
            this.newestEntry = newestEntry;
        }

        // Getters
        public long getTotalEntries() { return totalEntries; }
        public long getPinnedEntries() { return pinnedEntries; }
        public long getUnpinnedEntries() { return unpinnedEntries; }
        public LocalDateTime getOldestEntry() { return oldestEntry; }
        public LocalDateTime getNewestEntry() { return newestEntry; }

        // Setters
        public void setTotalEntries(long totalEntries) { this.totalEntries = totalEntries; }
        public void setPinnedEntries(long pinnedEntries) { this.pinnedEntries = pinnedEntries; }
        public void setUnpinnedEntries(long unpinnedEntries) { this.unpinnedEntries = unpinnedEntries; }
        public void setOldestEntry(LocalDateTime oldestEntry) { this.oldestEntry = oldestEntry; }
        public void setNewestEntry(LocalDateTime newestEntry) { this.newestEntry = newestEntry; }

        // Builder
        public static ClipboardStatsBuilder builder() {
            return new ClipboardStatsBuilder();
        }

        public static class ClipboardStatsBuilder {
            private long totalEntries;
            private long pinnedEntries;
            private long unpinnedEntries;
            private LocalDateTime oldestEntry;
            private LocalDateTime newestEntry;

            public ClipboardStatsBuilder totalEntries(long totalEntries) { this.totalEntries = totalEntries; return this; }
            public ClipboardStatsBuilder pinnedEntries(long pinnedEntries) { this.pinnedEntries = pinnedEntries; return this; }
            public ClipboardStatsBuilder unpinnedEntries(long unpinnedEntries) { this.unpinnedEntries = unpinnedEntries; return this; }
            public ClipboardStatsBuilder oldestEntry(LocalDateTime oldestEntry) { this.oldestEntry = oldestEntry; return this; }
            public ClipboardStatsBuilder newestEntry(LocalDateTime newestEntry) { this.newestEntry = newestEntry; return this; }

            public ClipboardStats build() {
                return new ClipboardStats(totalEntries, pinnedEntries, unpinnedEntries, oldestEntry, newestEntry);
            }
        }
    }
}