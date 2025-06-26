
**Create a JavaFX clipboard manager that exactly mimics the Windows 11 clipboard panel design, using TiwulFX-Dock library for docking functionality:**

### Project Setup:
```xml
<!-- Add to pom.xml -->
<dependency>
   <groupId>com.panemu</groupId>
   <artifactId>tiwulfx-dock</artifactId>
   <version>0.5.0</version> <!-- Check latest version -->
</dependency>
```

### Visual Requirements (Windows 11 Style):
1. **Color Scheme:**
    - Dark background: `#202020` (main background)
    - Panel background: `#2D2D2D` (for clipboard entries)
    - Text: `#FFFFFF` (primary), `#B0B0B0` (secondary)
    - Hover state: `#353535`
    - No colored elements - pure monochrome

2. **Window Specifications:**
    - Width: 360px (fixed like Windows clipboard)
    - Height: 100% of screen height minus taskbar
    - Position: Docked to right edge of screen
    - Semi-transparent with acrylic blur effect

### Implementation Structure:

```xml
<!-- ClipboardDock.fxml -->
<?xml version="1.0" encoding="UTF-8"?>
<?import com.panemu.tiwulfx.control.dock.*?>
<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>

<BorderPane xmlns="http://javafx.com/javafx/17" 
            xmlns:fx="http://javafx.com/fxml/1"
            fx:controller="ClipboardDockController"
            styleClass="clipboard-root">
    
    <!-- For docking capability -->
    <center>
        <SplitPane orientation="VERTICAL" styleClass="dock-container">
            <DetachableTabPane fx:id="dockTabPane" 
                              tabClosingPolicy="UNAVAILABLE"
                              styleClass="clipboard-dock">
                <Tab text="Clipboard" closable="false">
                    <VBox styleClass="clipboard-panel" spacing="0">
                        <!-- Header -->
                        <HBox styleClass="clipboard-header">
                            <Label text="Clipboard" styleClass="header-title"/>
                            <Region HBox.hgrow="ALWAYS"/>
                            <Button text="Clear all" 
                                    styleClass="text-button"
                                    onAction="#clearAll"/>
                        </HBox>
                        
                        <Separator styleClass="header-separator"/>
                        
                        <!-- Scrollable content -->
                        <ScrollPane fitToWidth="true" 
                                    VBox.vgrow="ALWAYS"
                                    styleClass="entries-scroll">
                            <VBox fx:id="entriesContainer" 
                                  styleClass="entries-container"
                                  spacing="8"/>
                        </ScrollPane>
                    </VBox>
                </Tab>
            </DetachableTabPane>
        </SplitPane>
    </center>
</BorderPane>
```

### Entry Component Template:
```java
// ClipboardEntryComponent.java
private Node createEntry(ClipboardEntry entry) {
    VBox entryBox = new VBox();
    entryBox.getStyleClass().add("clipboard-entry");
    
    // Header row
    HBox header = new HBox();
    header.setAlignment(Pos.CENTER_LEFT);
    
    // Content preview
    Label content = new Label(truncate(entry.getContent(), 50));
    content.getStyleClass().add("entry-content");
    HBox.setHgrow(content, Priority.ALWAYS);
    
    // Menu button (three dots)
    Button menuBtn = new Button("···");
    menuBtn.getStyleClass().addAll("icon-button", "menu-button");
    
    header.getChildren().addAll(content, menuBtn);
    
    // Footer row
    HBox footer = new HBox(10);
    footer.getStyleClass().add("entry-footer");
    
    Label timestamp = new Label(getRelativeTime(entry.getTimestamp()));
    timestamp.getStyleClass().add("entry-meta");
    
    Button pinBtn = new Button();
    pinBtn.getStyleClass().addAll("icon-button", "pin-button");
    pinBtn.setGraphic(new FontIcon(entry.isPinned() ? "mdi-pin" : "mdi-pin-outline"));
    
    footer.getChildren().addAll(timestamp, new Region(), pinBtn);
    HBox.setHgrow(footer.getChildren().get(1), Priority.ALWAYS);
    
    entryBox.getChildren().addAll(header, footer);
    return entryBox;
}
```

### CSS Styling (clipboard-dock.css):
```css
/* Root styling */
.clipboard-root {
    -fx-background-color: transparent;
}

.dock-container {
    -fx-background-color: #202020;
}

/* Remove tab header for cleaner look */
.clipboard-dock .tab-header-area {
    -fx-max-height: 0;
    -fx-pref-height: 0;
    -fx-opacity: 0;
}

/* Main panel */
.clipboard-panel {
    -fx-background-color: #202020;
    -fx-font-family: "Segoe UI", system;
}

/* Header */
.clipboard-header {
    -fx-padding: 16 16 12 16;
    -fx-alignment: center-left;
}

.header-title {
    -fx-text-fill: white;
    -fx-font-size: 20px;
}

.header-separator {
    -fx-background-color: #404040;
    -fx-padding: 0 16;
}

/* Text button style (Windows 11) */
.text-button {
    -fx-background-color: transparent;
    -fx-text-fill: #B0B0B0;
    -fx-font-size: 12px;
    -fx-padding: 4 8;
    -fx-cursor: hand;
}

.text-button:hover {
    -fx-text-fill: white;
    -fx-background-color: #353535;
    -fx-background-radius: 4;
}

/* Scrollpane */
.entries-scroll {
    -fx-background-color: transparent;
    -fx-padding: 8 0;
}

.entries-scroll .viewport {
    -fx-background-color: transparent;
}

/* Entry styling */
.entries-container {
    -fx-padding: 0 8;
}

.clipboard-entry {
    -fx-background-color: #2D2D2D;
    -fx-background-radius: 8;
    -fx-padding: 12;
    -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);
}

.clipboard-entry:hover {
    -fx-background-color: #353535;
    -fx-cursor: hand;
}

.entry-content {
    -fx-text-fill: white;
    -fx-font-size: 13px;
}

.entry-meta {
    -fx-text-fill: #B0B0B0;
    -fx-font-size: 11px;
}

/* Icon buttons */
.icon-button {
    -fx-background-color: transparent;
    -fx-padding: 4;
    -fx-cursor: hand;
    -fx-font-size: 16px;
}

.icon-button:hover {
    -fx-background-color: #404040;
    -fx-background-radius: 4;
}

.menu-button {
    -fx-text-fill: #B0B0B0;
}

.pin-button {
    -fx-text-fill: #B0B0B0;
}

.pin-button.pinned {
    -fx-text-fill: white;
}

/* Scrollbar styling (Windows 11 style) */
.scroll-bar {
    -fx-background-color: transparent;
    -fx-background-radius: 8;
    -fx-pref-width: 8;
}

.scroll-bar .thumb {
    -fx-background-color: #606060;
    -fx-background-radius: 8;
}

.scroll-bar .thumb:hover {
    -fx-background-color: #808080;
}

.scroll-bar .increment-button,
.scroll-bar .decrement-button {
    -fx-opacity: 0;
    -fx-pref-height: 0;
}
```

### Controller Setup for Docking:
```java
@FXML private DetachableTabPane dockTabPane;

@Override
public void initialize(URL location, ResourceBundle resources) {
    // Configure docking behavior
    configureDocking();
    
    // Load clipboard entries
    loadClipboardEntries();
}

private void configureDocking() {
    // Set stage owner
    dockTabPane.setStageOwnerFactory(stage -> getScene().getWindow());
    
    // Configure detached window appearance
    dockTabPane.setSceneFactory(param -> {
        BorderPane root = new BorderPane();
        root.setCenter(param);
        root.getStyleClass().add("detached-window");
        
        Scene scene = new Scene(root, 360, 700);
        scene.getStylesheets().add(getClass().getResource("/clipboard-dock.css").toExternalForm());
        scene.setFill(Color.TRANSPARENT);
        
        return scene;
    });
    
    // Handle dock/undock events
    dockTabPane.setOnClosedPassSibling(sibling -> {
        // Handle when tab is closed
    });
    
    // Custom drop hint for Windows 11 style
    dockTabPane.setDropHint(new WindowsStyleDropHint());
}

// Auto-hide functionality when docked
private void setupAutoHide(Stage stage) {
    Timeline hideTimeline = new Timeline(
        new KeyFrame(Duration.seconds(2), e -> {
            if (!stage.isFocused()) {
                // Slide out animation
                TranslateTransition slide = new TranslateTransition(Duration.millis(300), stage.getScene().getRoot());
                slide.setToX(340); // Hide most of the window
                slide.play();
            }
        })
    );
    
    stage.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
        if (isFocused) {
            // Slide in animation
            TranslateTransition slide = new TranslateTransition(Duration.millis(300), stage.getScene().getRoot());
            slide.setToX(0);
            slide.play();
            hideTimeline.stop();
        } else {
            hideTimeline.playFromStart();
        }
    });
}
```

### Key Features to Implement:
1. **Docking Behavior:**
    - Use TiwulFX DetachableTabPane for dock/undock capability
    - Default dock position: right edge of screen
    - Support dragging to left/right edges
    - Save dock state in preferences

2. **Auto-hide When Docked:**
    - Slide out after 2 seconds of inactivity
    - Show on mouse hover near edge
    - Smooth slide animations (300ms)

3. **Windows 11 Clipboard Behavior:**
    - Click entry to copy content
    - Pin button (no color change, just icon change)
    - Three-dot menu for entry options
    - Clear all functionality
    - No selection states, only hover

4. **Window Properties When Docked:**
    - StageStyle.UNDECORATED
    - Always on top
    - Semi-transparent background
    - No resize handles
    - Blur effect if possible

The implementation should perfectly mimic Windows 11's clipboard panel aesthetics while leveraging TiwulFX-Dock for the docking functionality. The dock feature should feel native and seamless, with smooth animations and proper edge detection.