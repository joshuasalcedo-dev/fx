package io.joshuasalcedo.fx.api;

import io.joshuasalcedo.fx.domain.clipboard.ClipboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/local/clipboards/export")
@CrossOrigin(originPatterns = "*", maxAge = 3600)
@Tag(
    name = "Clipboard Export",
    description = "API for exporting clipboard entries in different formats")
public class ClipboardExportController {
  private static final Logger log = LoggerFactory.getLogger(ClipboardExportController.class);

  private final ClipboardService clipboardService;

  public ClipboardExportController(ClipboardService clipboardService) {
    this.clipboardService = clipboardService;
  }

  @Operation(
      summary = "Export clipboard entries as JSON",
      description = "Exports clipboard entries in JSON format")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully exported clipboard entries",
            content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "500", description = "Export failed")
      })
  @GetMapping("/json")
  public CompletableFuture<ResponseEntity<String>> exportAsJson(
      @Parameter(description = "Whether to include pinned entries in export", example = "true")
          @RequestParam(defaultValue = "true")
          boolean includePinned) {

    log.info("Exporting clipboard entries as JSON - includePinned: {}", includePinned);

    return clipboardService
        .exportEntries(ClipboardService.ExportFormat.JSON, includePinned)
        .thenApply(
            content -> {
              String filename = generateFilename("clipboard_export", "json");

              return ResponseEntity.ok()
                  .header(
                      HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                  .contentType(MediaType.APPLICATION_JSON)
                  .body(content);
            })
        .exceptionally(
            ex -> {
              log.error("Failed to export as JSON", ex);
              return ResponseEntity.internalServerError().body("{\"error\":\"Export failed\"}");
            });
  }

  @Operation(
      summary = "Export clipboard entries as CSV",
      description = "Exports clipboard entries in CSV format")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully exported clipboard entries",
            content = @Content(mediaType = "text/csv")),
        @ApiResponse(responseCode = "500", description = "Export failed")
      })
  @GetMapping("/csv")
  public CompletableFuture<ResponseEntity<String>> exportAsCsv(
      @Parameter(description = "Whether to include pinned entries in export", example = "true")
          @RequestParam(defaultValue = "true")
          boolean includePinned) {

    log.info("Exporting clipboard entries as CSV - includePinned: {}", includePinned);

    return clipboardService
        .exportEntries(ClipboardService.ExportFormat.CSV, includePinned)
        .thenApply(
            content -> {
              String filename = generateFilename("clipboard_export", "csv");

              return ResponseEntity.ok()
                  .header(
                      HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                  .contentType(MediaType.parseMediaType("text/csv"))
                  .body(content);
            })
        .exceptionally(
            ex -> {
              log.error("Failed to export as CSV", ex);
              return ResponseEntity.internalServerError().body("Export failed");
            });
  }

  @Operation(
      summary = "Export clipboard entries as text",
      description = "Exports clipboard entries in plain text format")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully exported clipboard entries",
            content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "500", description = "Export failed")
      })
  @GetMapping("/txt")
  public CompletableFuture<ResponseEntity<String>> exportAsText(
      @Parameter(description = "Whether to include pinned entries in export", example = "true")
          @RequestParam(defaultValue = "true")
          boolean includePinned) {

    log.info("Exporting clipboard entries as text - includePinned: {}", includePinned);

    return clipboardService
        .exportEntries(ClipboardService.ExportFormat.TXT, includePinned)
        .thenApply(
            content -> {
              String filename = generateFilename("clipboard_export", "txt");

              return ResponseEntity.ok()
                  .header(
                      HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                  .contentType(MediaType.TEXT_PLAIN)
                  .body(content);
            })
        .exceptionally(
            ex -> {
              log.error("Failed to export as text", ex);
              return ResponseEntity.internalServerError().body("Export failed");
            });
  }

  private String generateFilename(String prefix, String extension) {
    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
    return String.format("%s_%s.%s", prefix, timestamp, extension);
  }
}
