package io.joshuasalcedo.fx.application.clipboard;

import io.joshuasalcedo.fx.domain.clipboard.ClipboardEntry;
import io.joshuasalcedo.fx.domain.clipboard.ClipboardService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Windows 11-style clipboard entry component builder.
 * Creates individual clipboard entry UI components that mimic the exact look and behavior
 * of Windows 11 clipboard panel entries.
 */
public class ClipboardEntryComponent {

    private static final Logger logger = LoggerFactory.getLogger(ClipboardEntryComponent.class);
    
    private final ClipboardService clipboardService;
    private final Runnable refreshCallback;

    public ClipboardEntryComponent(ClipboardService clipboardService, Runnable refreshCallback) {
        this.clipboardService = clipboardService;
        this.refreshCallback = refreshCallback;
    }

    /**
     * Creates a Windows 11-style clipboard entry component.
     * 
     * @param entry The clipboard entry data
     * @return A Node representing the clipboard entry UI
     */
    public Node createEntry(ClipboardEntry entry) {
        VBox entryBox = new VBox();
        entryBox.getStyleClass().add("clipboard-entry");
        entryBox.setPadding(new Insets(12));
        entryBox.setCursor(javafx.scene.Cursor.HAND);
        
        // Header row with content and menu button
        HBox header = createHeader(entry);
        
        // Footer row with timestamp and pin button
        HBox footer = createFooter(entry);
        
        entryBox.getChildren().addAll(header, footer);
        
        // Click to copy functionality (Windows 11 behavior)
        entryBox.setOnMouseClicked(e -> {
            if (e.getClickCount() == 1) {
                copyToClipboard(entry.getContent());
                // Optional: Add visual feedback for copy action
                flashCopyFeedback(entryBox);
            }
        });
        
        return entryBox;
    }

    private HBox createHeader(ClipboardEntry entry) {
        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);
        
        // Content preview with proper wrapping
        Label content = new Label(truncateContent(entry.getContent(), 100));
        content.getStyleClass().add("entry-content");
        content.setWrapText(true);
        content.setMaxWidth(280); // Ensure it doesn't exceed container width
        HBox.setHgrow(content, Priority.ALWAYS);
        
        // Menu button (three dots) - Windows 11 style
        Button menuBtn = createMenuButton(entry);
        
        header.getChildren().addAll(content, menuBtn);
        return header;
    }

    private HBox createFooter(ClipboardEntry entry) {
        HBox footer = new HBox(10);
        footer.getStyleClass().add("entry-footer");
        footer.setPadding(new Insets(8, 0, 0, 0));
        footer.setAlignment(Pos.CENTER_LEFT);
        
        // Timestamp
        Label timestamp = new Label(getRelativeTime(entry.getTimestamp()));
        timestamp.getStyleClass().add("entry-meta");
        
        // Spacer to push pin button to the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Pin button
        Button pinBtn = createPinButton(entry);
        
        footer.getChildren().addAll(timestamp, spacer, pinBtn);
        return footer;
    }

    private Button createMenuButton(ClipboardEntry entry) {
        Button menuBtn = new Button("···");
        menuBtn.getStyleClass().addAll("icon-button", "menu-button");
        menuBtn.setOnAction(e -> showEntryContextMenu(menuBtn, entry));
        
        // Prevent event bubbling to parent
        menuBtn.setOnMouseClicked(e -> e.consume());
        
        return menuBtn;
    }

    private Button createPinButton(ClipboardEntry entry) {
        Button pinBtn = new Button();
        pinBtn.getStyleClass().addAll("icon-button", "pin-button");
        
        if (entry.isPinned()) {
            pinBtn.getStyleClass().add("pinned");
        }
        
        FontIcon pinIcon = new FontIcon(entry.isPinned() ? "mdi2p-pin" : "mdi2p-pin-outline");
        pinIcon.setIconSize(14);
        pinBtn.setGraphic(pinIcon);
        
        pinBtn.setOnAction(e -> {
            togglePin(entry, pinBtn, pinIcon);
            e.consume(); // Prevent bubbling to parent
        });
        
        // Prevent event bubbling to parent
        pinBtn.setOnMouseClicked(e -> e.consume());
        
        return pinBtn;
    }

    private void showEntryContextMenu(Node node, ClipboardEntry entry) {
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getStyleClass().add("context-menu");
        
        // Copy item
        MenuItem copyItem = new MenuItem("Copy");
        FontIcon copyIcon = new FontIcon("mdi2c-content-copy");
        copyIcon.setIconSize(14);
        copyItem.setGraphic(copyIcon);
        copyItem.setOnAction(e -> copyToClipboard(entry.getContent()));
        
        // Pin/Unpin item
        MenuItem pinItem = new MenuItem(entry.isPinned() ? "Unpin" : "Pin");
        FontIcon pinIcon = new FontIcon(entry.isPinned() ? "mdi2p-pin-off" : "mdi2p-pin");
        pinIcon.setIconSize(14);
        pinItem.setGraphic(pinIcon);
        pinItem.setOnAction(e -> {
            entry.setPinned(!entry.isPinned());
            clipboardService.save(entry);
            refreshCallback.run();
        });
        
        // Delete item
        MenuItem deleteItem = new MenuItem("Delete");
        FontIcon deleteIcon = new FontIcon("mdi2d-delete");
        deleteIcon.setIconSize(14);
        deleteItem.setGraphic(deleteIcon);
        deleteItem.setOnAction(e -> {
            clipboardService.delete(entry.getId());
            refreshCallback.run();
        });
        
        contextMenu.getItems().addAll(copyItem, pinItem, deleteItem);
        
        // Show context menu
        contextMenu.show(node, javafx.geometry.Side.RIGHT, 0, 0);
    }

    private void togglePin(ClipboardEntry entry, Button pinBtn, FontIcon pinIcon) {
        try {
            entry.setPinned(!entry.isPinned());
            clipboardService.save(entry);
            
            // Update UI immediately
            if (entry.isPinned()) {
                pinBtn.getStyleClass().add("pinned");
                pinIcon.setIconLiteral("mdi2p-pin");
            } else {
                pinBtn.getStyleClass().remove("pinned");
                pinIcon.setIconLiteral("mdi2p-pin-outline");
            }
            
            logger.debug("Toggled pin status for entry: {}", entry.getId());
            
        } catch (Exception e) {
            logger.error("Failed to toggle pin status", e);
        }
    }

    private void copyToClipboard(String content) {
        try {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putString(content);
            clipboard.setContent(clipboardContent);
            
            logger.debug("Copied to clipboard: {}", truncateContent(content, 50));
            
        } catch (Exception e) {
            logger.error("Failed to copy to clipboard", e);
        }
    }

    private void flashCopyFeedback(VBox entryBox) {
        // Optional: Add a subtle flash effect to indicate copy action
        // This mimics Windows 11 behavior where there's visual feedback
        entryBox.getStyleClass().add("copying");
        
        // Remove the class after a short delay
        javafx.application.Platform.runLater(() -> {
            try {
                Thread.sleep(150);
                javafx.application.Platform.runLater(() -> {
                    entryBox.getStyleClass().remove("copying");
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    /**
     * Truncates content to specified length with ellipsis.
     */
    public static String truncateContent(String content, int maxLength) {
        if (content == null) return "";
        if (content.length() <= maxLength) return content;
        return content.substring(0, maxLength - 3) + "...";
    }

    /**
     * Formats timestamp into human-readable relative time.
     */
    public static String getRelativeTime(LocalDateTime dateTime) {
        if (dateTime == null) return "Unknown";
        
        LocalDateTime now = LocalDateTime.now();
        long minutes = ChronoUnit.MINUTES.between(dateTime, now);
        long hours = ChronoUnit.HOURS.between(dateTime, now);
        long days = ChronoUnit.DAYS.between(dateTime, now);
        
        if (minutes < 1) {
            return "Just now";
        } else if (minutes < 60) {
            return minutes + " minute" + (minutes == 1 ? "" : "s") + " ago";
        } else if (hours < 24) {
            return hours + " hour" + (hours == 1 ? "" : "s") + " ago";
        } else if (days < 7) {
            return days + " day" + (days == 1 ? "" : "s") + " ago";
        } else {
            return dateTime.format(DateTimeFormatter.ofPattern("MMM d, yyyy"));
        }
    }

    /**
     * Determines content type based on the clipboard content.
     * This can be used for showing different icons or formatting.
     */
    public static String getContentType(String content) {
        if (content == null || content.isEmpty()) {
            return "empty";
        }
        
        // URL detection
        if (content.startsWith("http://") || content.startsWith("https://")) {
            return "url";
        }
        
        // Email detection
        if (content.contains("@") && content.contains(".")) {
            return "email";
        }
        
        // Number detection
        try {
            Double.parseDouble(content.trim());
            return "number";
        } catch (NumberFormatException e) {
            // Not a number
        }
        
        // Multiline text
        if (content.contains("\n")) {
            return "multiline";
        }
        
        return "text";
    }
}