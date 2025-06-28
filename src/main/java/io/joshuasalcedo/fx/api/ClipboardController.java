package io.joshuasalcedo.fx.api;

import io.joshuasalcedo.fx.domain.clipboard.ClipboardEntry;
import io.joshuasalcedo.fx.domain.clipboard.ClipboardService;
import io.joshuasalcedo.fx.presentation.dto.ClipboardDto;
import io.joshuasalcedo.fx.presentation.request.TogglePinRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/local/clipboards")
@CrossOrigin(originPatterns = "*", maxAge = 3600)
@Validated
@Tag(name = "Clipboard", description = "Clipboard management API")
public class ClipboardController {
    private static final Logger log = LoggerFactory.getLogger(ClipboardController.class);
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;

    private final ClipboardService clipboardService;

    public ClipboardController(ClipboardService clipboardService) {
        this.clipboardService = clipboardService;
    }

    @Operation(summary = "Get all clipboard entries", description = "Returns a paginated list of clipboard entries sorted by timestamp in descending order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved clipboard entries",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    })
    @GetMapping
    public ResponseEntity<Page<ClipboardDto>> clipboards(
            @Parameter(description = "Page number (zero-based)", example = "0") @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Number of items per page (max 100)", example = "20") @RequestParam(name = "max", defaultValue = "20") @Min(1) int size) {

        log.debug("Getting clipboards - page: {}, size: {}", page, size);

        // Limit page size to prevent performance issues
        size = Math.min(size, MAX_PAGE_SIZE);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));

        Page<ClipboardEntry> entries = clipboardService.findAll(pageable);
        Page<ClipboardDto> dtos = entries.map(this::toDto);

        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Get pinned clipboard entries", description = "Returns a list of all pinned clipboard entries")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved pinned clipboard entries",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    })
    @GetMapping("/pins")
    public ResponseEntity<List<ClipboardDto>> pinnedClipboards() {
        log.debug("Getting pinned clipboards");

        List<ClipboardEntry> pinnedEntries = clipboardService.findAllPinned();
        List<ClipboardDto> dtos = pinnedEntries.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Toggle pin status", description = "Toggles the pin status of a clipboard entry")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully toggled pin status",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClipboardDto.class))),
        @ApiResponse(responseCode = "404", description = "Clipboard entry not found")
    })
    @PutMapping("/pin")
    public ResponseEntity<ClipboardDto> pinClipboards(
            @Parameter(description = "Toggle pin request with entry ID", required = true)
            @Valid @RequestBody TogglePinRequest request) {
        log.debug("Toggling pin for clipboard entry with id: {}", request.id());

        return clipboardService.findById(request.id())
                .map(entry -> {
                    // Toggle the pin status
                    boolean newPinStatus = !entry.isPinned();
                    return clipboardService.setPinned(request.id(), newPinStatus)
                            .map(updated -> ResponseEntity.ok(toDto(updated)))
                            .orElse(ResponseEntity.notFound().build());
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete all clipboard entries", description = "Deletes all clipboard entries, optionally including pinned entries")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Successfully deleted clipboard entries")
    })
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteAllClipboards(
            @Parameter(description = "Whether to include pinned entries in deletion", example = "false")
            @RequestParam(defaultValue = "false") boolean includePinned) {
        log.info("Deleting all clipboards - includePinned: {}", includePinned);

        long deletedCount = clipboardService.deleteAll(includePinned);
        log.info("Deleted {} clipboard entries", deletedCount);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete a clipboard entry", description = "Deletes a specific clipboard entry by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Successfully deleted the clipboard entry"),
        @ApiResponse(responseCode = "404", description = "Clipboard entry not found")
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteClipboard(
            @Parameter(description = "ID of the clipboard entry to delete", required = true, example = "1")
            @PathVariable @NotNull Long id) {
        log.debug("Deleting clipboard entry with id: {}", id);

        boolean deleted = clipboardService.deleteById(id);

        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/stop")
    public ResponseEntity<StopClipboardResponse> stopClipboard() {
        log.info("Stopping clipboard monitoring");

        boolean stopped = clipboardService.stopClipboard();

        if (stopped) {
            return ResponseEntity.ok(new StopClipboardResponse(true, "Clipboard monitoring stopped successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new StopClipboardResponse(false, "Failed to stop clipboard monitoring"));
        }
    }

    @Operation(summary = "Search clipboard entries", description = "Searches clipboard entries by content")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved search results",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    })
    @GetMapping("/search")
    public ResponseEntity<Page<ClipboardDto>> searchClipboards(
            @Parameter(description = "Search query", required = true, example = "example text")
            @RequestParam String query,
            @Parameter(description = "Page number (zero-based)", example = "0")
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Number of items per page (max 100)", example = "20")
            @RequestParam(defaultValue = "20") @Min(1) int size) {

        log.debug("Searching clipboards with query: '{}', page: {}, size: {}", query, page, size);

        size = Math.min(size, MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));

        Page<ClipboardEntry> entries = clipboardService.searchByContent(query, pageable);
        Page<ClipboardDto> dtos = entries.map(this::toDto);

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<ClipboardDto>> getRecentClipboards(
            @RequestParam(defaultValue = "24") @Min(1) int hours) {

        log.debug("Getting clipboard entries from the last {} hours", hours);

        List<ClipboardEntry> recentEntries = clipboardService.findRecent(hours);
        List<ClipboardDto> dtos = recentEntries.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/stats")
    public ResponseEntity<ClipboardService.ClipboardStats> getClipboardStats() {
        log.debug("Getting clipboard statistics");

        ClipboardService.ClipboardStats stats = clipboardService.getStats();
        return ResponseEntity.ok(stats);
    }

    // Helper method to convert entity to DTO
    private ClipboardDto toDto(ClipboardEntry entry) {
        return new ClipboardDto(
                entry.getId(),
                entry.getTimestamp(),
                entry.getContent(),
                entry.isPinned()
        );
    }

    // Response class for stop operation
    public record StopClipboardResponse(boolean success, String message) {}
}
