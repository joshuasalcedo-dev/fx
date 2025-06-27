package io.joshuasalcedo.fx.domain.clipboard;

import io.joshuasalcedo.clipboard.core.ClipboardMonitor;
import io.joshuasalcedo.fx.infrastructure.events.ClipboardRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClipboardServiceTest {

    @Mock
    private ClipboardRepository clipboardRepository;

    private ClipboardService clipboardService;

    @Mock
    private ClipboardMonitor clipboardMonitor;
    @Mock
    ApplicationEventPublisher applicationEventPublisher;

    @BeforeEach
    void setUp() {
        clipboardService = new ClipboardService(clipboardRepository,clipboardMonitor,applicationEventPublisher);
    }

    @Test
    void save_ValidContent_SavesNewEntry() {
        // Arrange
        String content = "Test content";
        ClipboardEntry newEntry = ClipboardEntry.builder()
                .content(content)
                .timestamp(LocalDateTime.now())
                .build();
        ClipboardEntry savedEntry = ClipboardEntry.builder()
                .id(1L)
                .content(content)
                .timestamp(LocalDateTime.now())
                .contentLength(content.length())
                .build();

        when(clipboardRepository.findFirstByContentAndTimestampAfter(anyString(), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());
        when(clipboardRepository.save(any(ClipboardEntry.class))).thenReturn(savedEntry);

        // Act
        ClipboardEntry result = clipboardService.save(content);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(content, result.getContent());

        ArgumentCaptor<ClipboardEntry> entryCaptor = ArgumentCaptor.forClass(ClipboardEntry.class);
        verify(clipboardRepository).save(entryCaptor.capture());
        assertEquals(content, entryCaptor.getValue().getContent());
    }

    @Test
    void save_DuplicateContent_UpdatesExistingEntry() {
        // Arrange
        String content = "Duplicate content";
        LocalDateTime oldTimestamp = LocalDateTime.now().minusHours(1);
        ClipboardEntry existingEntry = ClipboardEntry.builder()
                .id(1L)
                .content(content)
                .timestamp(oldTimestamp)
                .build();

        when(clipboardRepository.findFirstByContentAndTimestampAfter(anyString(), any(LocalDateTime.class)))
                .thenReturn(Optional.of(existingEntry));
        when(clipboardRepository.save(any(ClipboardEntry.class))).thenReturn(existingEntry);

        // Act
        ClipboardEntry result = clipboardService.save(content);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(content, result.getContent());
        assertTrue(result.getTimestamp().isAfter(oldTimestamp));

        verify(clipboardRepository).save(existingEntry);
    }

    @Test
    void save_EmptyContent_ThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> clipboardService.save(""));
        assertThrows(IllegalArgumentException.class, () -> clipboardService.save((String) null));

        verify(clipboardRepository, never()).save(any(ClipboardEntry.class));
    }

    @Test
    void setPinned_ExistingEntry_UpdatesPinnedStatus() {
        // Arrange
        Long id = 1L;
        ClipboardEntry entry = ClipboardEntry.builder()
                .id(id)
                .content("Test content")
                .isPinned(false)
                .build();

        when(clipboardRepository.findById(id)).thenReturn(Optional.of(entry));
        when(clipboardRepository.save(any(ClipboardEntry.class))).thenReturn(entry);

        // Act
        Optional<ClipboardEntry> result = clipboardService.setPinned(id, true);

        // Assert
        assertTrue(result.isPresent());
        assertTrue(result.get().isPinned());

        verify(clipboardRepository).save(entry);
    }

    @Test
    void setPinned_NonExistingEntry_ReturnsEmpty() {
        // Arrange
        Long id = 999L;
        when(clipboardRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        Optional<ClipboardEntry> result = clipboardService.setPinned(id, true);

        // Assert
        assertTrue(result.isEmpty());
        verify(clipboardRepository, never()).save(any(ClipboardEntry.class));
    }

    @Test
    void pin_CallsSetPinnedWithTrue() {
        // Arrange
        Long id = 1L;
        ClipboardEntry entry = ClipboardEntry.builder()
                .id(id)
                .content("Test content")
                .isPinned(false)
                .build();

        when(clipboardRepository.findById(id)).thenReturn(Optional.of(entry));
        when(clipboardRepository.save(any(ClipboardEntry.class))).thenReturn(entry);

        // Act
        clipboardService.pin(id);

        // Assert
        verify(clipboardRepository).findById(id);
        verify(clipboardRepository).save(argThat(e -> e.isPinned()));
    }

    @Test
    void unpin_CallsSetPinnedWithFalse() {
        // Arrange
        Long id = 1L;
        ClipboardEntry entry = ClipboardEntry.builder()
                .id(id)
                .content("Test content")
                .isPinned(true)
                .build();

        when(clipboardRepository.findById(id)).thenReturn(Optional.of(entry));
        when(clipboardRepository.save(any(ClipboardEntry.class))).thenReturn(entry);

        // Act
        clipboardService.unpin(id);

        // Assert
        verify(clipboardRepository).findById(id);
        verify(clipboardRepository).save(argThat(e -> !e.isPinned()));
    }

    @Test
    void deleteAll_IncludePinnedTrue_DeletesAllEntries() {
        // Arrange
        when(clipboardRepository.count()).thenReturn(10L);

        // Act
        long result = clipboardService.deleteAll(true);

        // Assert
        assertEquals(10L, result);
        verify(clipboardRepository).deleteAll();
        verify(clipboardRepository, never()).deleteByIsPinnedFalse();
    }

    @Test
    void deleteAll_IncludePinnedFalse_DeletesOnlyUnpinnedEntries() {
        // Arrange
        when(clipboardRepository.count()).thenReturn(10L);
        when(clipboardRepository.deleteByIsPinnedFalse()).thenReturn(7);

        // Act
        long result = clipboardService.deleteAll(false);

        // Assert
        assertEquals(7L, result);
        verify(clipboardRepository, never()).deleteAll();
        verify(clipboardRepository).deleteByIsPinnedFalse();
    }

    @Test
    void testDeleteAll_CallsDeleteAllWithFalse() {
        // Arrange
        when(clipboardRepository.count()).thenReturn(10L);
        when(clipboardRepository.deleteByIsPinnedFalse()).thenReturn(7);

        // Act
        clipboardService.deleteAll();

        // Assert
        verify(clipboardRepository).deleteByIsPinnedFalse();
    }

    @Test
    void deleteById_ExistingEntry_DeletesAndReturnsTrue() {
        // Arrange
        Long id = 1L;
        when(clipboardRepository.existsById(id)).thenReturn(true);

        // Act
        boolean result = clipboardService.deleteById(id);

        // Assert
        assertTrue(result);
        verify(clipboardRepository).deleteById(id);
    }

    @Test
    void deleteById_NonExistingEntry_ReturnsFalse() {
        // Arrange
        Long id = 999L;
        when(clipboardRepository.existsById(id)).thenReturn(false);

        // Act
        boolean result = clipboardService.deleteById(id);

        // Assert
        assertFalse(result);
        verify(clipboardRepository, never()).deleteById(id);
    }

    @Test
    void findById_ExistingEntry_ReturnsEntry() {
        // Arrange
        Long id = 1L;
        ClipboardEntry entry = ClipboardEntry.builder()
                .id(id)
                .content("Test content")
                .build();

        when(clipboardRepository.findById(id)).thenReturn(Optional.of(entry));

        // Act
        Optional<ClipboardEntry> result = clipboardService.findById(id);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
    }

    @Test
    void findById_NonExistingEntry_ReturnsEmpty() {
        // Arrange
        Long id = 999L;
        when(clipboardRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        Optional<ClipboardEntry> result = clipboardService.findById(id);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void findAll_ReturnsPageOfEntries() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<ClipboardEntry> entries = List.of(
            ClipboardEntry.builder().id(1L).content("Entry 1").build(),
            ClipboardEntry.builder().id(2L).content("Entry 2").build()
        );
        Page<ClipboardEntry> page = new PageImpl<>(entries, pageable, entries.size());

        when(clipboardRepository.findAll(pageable)).thenReturn(page);

        // Act
        Page<ClipboardEntry> result = clipboardService.findAll(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(entries, result.getContent());
    }

    @Test
    void findAllPinned_ReturnsPinnedEntries() {
        // Arrange
        List<ClipboardEntry> pinnedEntries = List.of(
            ClipboardEntry.builder().id(1L).content("Pinned 1").isPinned(true).build(),
            ClipboardEntry.builder().id(3L).content("Pinned 2").isPinned(true).build()
        );

        when(clipboardRepository.findByIsPinnedTrueOrderByTimestampDesc()).thenReturn(pinnedEntries);

        // Act
        List<ClipboardEntry> result = clipboardService.findAllPinned();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(pinnedEntries, result);
    }

    @Test
    void searchByContent_ValidTerm_ReturnsMatchingEntries() {
        // Arrange
        String searchTerm = "test";
        Pageable pageable = PageRequest.of(0, 10);
        List<ClipboardEntry> entries = List.of(
            ClipboardEntry.builder().id(1L).content("Test content").build(),
            ClipboardEntry.builder().id(2L).content("Another test").build()
        );
        Page<ClipboardEntry> page = new PageImpl<>(entries, pageable, entries.size());

        when(clipboardRepository.findByContentContainingIgnoreCase(searchTerm, pageable)).thenReturn(page);

        // Act
        Page<ClipboardEntry> result = clipboardService.searchByContent(searchTerm, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(entries, result.getContent());
    }

    @Test
    void searchByContent_EmptyTerm_ReturnsEmptyPage() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<ClipboardEntry> result = clipboardService.searchByContent("", pageable);

        // Assert
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        verify(clipboardRepository, never()).findByContentContainingIgnoreCase(anyString(), any(Pageable.class));
    }

    @Test
    void findRecent_ReturnsEntriesFromLastNHours() {
        // Arrange
        int hours = 24;
        List<ClipboardEntry> recentEntries = List.of(
            ClipboardEntry.builder().id(1L).content("Recent 1").build(),
            ClipboardEntry.builder().id(2L).content("Recent 2").build()
        );

        when(clipboardRepository.findByTimestampAfterOrderByTimestampDesc(any(LocalDateTime.class)))
                .thenReturn(recentEntries);

        // Act
        List<ClipboardEntry> result = clipboardService.findRecent(hours);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(recentEntries, result);

        ArgumentCaptor<LocalDateTime> timeCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(clipboardRepository).findByTimestampAfterOrderByTimestampDesc(timeCaptor.capture());
        LocalDateTime threshold = timeCaptor.getValue();
        LocalDateTime expectedThreshold = LocalDateTime.now().minusHours(hours);
        // Allow a small time difference due to test execution time
        assertTrue(Math.abs(threshold.until(expectedThreshold, java.time.temporal.ChronoUnit.SECONDS)) < 5);
    }

    @Test
    void deleteOlderThan_IncludePinnedTrue_DeletesAllOldEntries() {
        // Arrange
        int hours = 48;
        when(clipboardRepository.deleteByTimestampBefore(any(LocalDateTime.class))).thenReturn(5);

        // Act
        long result = clipboardService.deleteOlderThan(hours, true);

        // Assert
        assertEquals(5L, result);
        verify(clipboardRepository).deleteByTimestampBefore(any(LocalDateTime.class));
        verify(clipboardRepository, never()).deleteByTimestampBeforeAndIsPinnedFalse(any(LocalDateTime.class));
    }

    @Test
    void deleteOlderThan_IncludePinnedFalse_DeletesOnlyUnpinnedOldEntries() {
        // Arrange
        int hours = 48;
        when(clipboardRepository.deleteByTimestampBeforeAndIsPinnedFalse(any(LocalDateTime.class))).thenReturn(3);

        // Act
        long result = clipboardService.deleteOlderThan(hours, false);

        // Assert
        assertEquals(3L, result);
        verify(clipboardRepository, never()).deleteByTimestampBefore(any(LocalDateTime.class));
        verify(clipboardRepository).deleteByTimestampBeforeAndIsPinnedFalse(any(LocalDateTime.class));
    }

    @Test
    void getStats_ReturnsCorrectStatistics() {
        // Arrange
        long totalCount = 10L;
        long pinnedCount = 3L;
        LocalDateTime oldestTimestamp = LocalDateTime.now().minusDays(7);
        LocalDateTime newestTimestamp = LocalDateTime.now();

        when(clipboardRepository.count()).thenReturn(totalCount);
        when(clipboardRepository.countByisPinnedTrue()).thenReturn(pinnedCount);
        when(clipboardRepository.findOldestTimestamp()).thenReturn(oldestTimestamp);
        when(clipboardRepository.findNewestTimestamp()).thenReturn(newestTimestamp);

        // Act
        ClipboardService.ClipboardStats stats = clipboardService.getStats();

        // Assert
        assertNotNull(stats);
        assertEquals(totalCount, stats.getTotalEntries());
        assertEquals(pinnedCount, stats.getPinnedEntries());
        assertEquals(totalCount - pinnedCount, stats.getUnpinnedEntries());
        assertEquals(oldestTimestamp, stats.getOldestEntry());
        assertEquals(newestTimestamp, stats.getNewestEntry());
    }
}
