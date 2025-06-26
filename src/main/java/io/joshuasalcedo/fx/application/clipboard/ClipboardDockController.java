package io.joshuasalcedo.fx.application.clipboard;

import com.panemu.tiwulfx.control.dock.DetachableTabPane;
import io.joshuasalcedo.fx.domain.clipboard.ClipboardEntry;
import io.joshuasalcedo.fx.domain.clipboard.ClipboardService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

@Component
public class ClipboardDockController implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(ClipboardDockController.class);
    
    @FXML private DetachableTabPane dockTabPane;
    @FXML private VBox entriesContainer;
    @FXML private VBox emptyState;
    
    @Autowired private ClipboardService clipboardService;
    
    private Timeline autoHideTimeline;
    private Stage dockStage;
    private boolean isDocked = false;
    private boolean isAutoHiding = false;
    private ClipboardEntryComponent entryComponent;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            // Initialize the entry component
            entryComponent = new ClipboardEntryComponent(clipboardService, this::loadClipboardEntries);
            
            configureDocking();
            loadClipboardEntries();
            // Don't setup auto-hide by default since we're not docked
            // setupAutoHide();
        });
    }

    private void configureDocking() {
        try {
            // Get the current stage
            dockStage = (Stage) dockTabPane.getScene().getWindow();
            
            // Configure stage for Windows 11 dock behavior
            dockStage.initStyle(StageStyle.UTILITY);
            dockStage.setAlwaysOnTop(true);
            dockStage.setResizable(true);

            
            // Set window dimensions and position
            setupWindowDimensions();
            
            // Configure detached window appearance
            dockTabPane.setSceneFactory(param -> {
                BorderPane root = new BorderPane();
                root.setCenter(param);
                root.getStyleClass().add("detached-window");
                
                Scene scene = new Scene(root, 360, 700);
                scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/assets/clipboard-dock.css")).toExternalForm());
                scene.setFill(Color.TRANSPARENT);
                
                return scene;
            });
            
            // Handle dock/undock events
            dockTabPane.setOnClosedPassSibling(sibling -> {
                logger.debug("Tab closed, sibling: {}", sibling);
            });
            
            logger.info("Docking configured successfully");
            
        } catch (Exception e) {
            logger.error("Failed to configure docking", e);
        }
    }

    private void setupWindowDimensions() {
        if (dockStage == null) return;
        
        try {
            // Get screen dimensions
            Screen screen = Screen.getPrimary();
            double screenWidth = screen.getVisualBounds().getWidth();
            double screenHeight = screen.getVisualBounds().getHeight();
            double taskbarHeight = 40; // Approximate taskbar height
            
            // Set window size
            double windowWidth = 360;
            double windowHeight = screenHeight - taskbarHeight;
            
            dockStage.setWidth(windowWidth);
            dockStage.setHeight(windowHeight);
            
            // Position at right edge
            dockStage.setX(screenWidth - windowWidth);
            dockStage.setY(0);
            
            logger.info("Window positioned at: x={}, y={}, width={}, height={}", 
                       dockStage.getX(), dockStage.getY(), windowWidth, windowHeight);
            
        } catch (Exception e) {
            logger.error("Failed to setup window dimensions", e);
        }
    }

    private void setupAutoHide() {
        if (dockStage == null) return;
        
        try {
            autoHideTimeline = new Timeline(
                new KeyFrame(Duration.seconds(3), e -> {
                    if (!dockStage.isFocused() && !isMouseOverStage() && isDocked) {
                        slideOut();
                    }
                })
            );
            
            // Listen for focus changes
            dockStage.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
                if (isFocused && isAutoHiding) {
                    slideIn();
                } else if (!isFocused && isDocked) {
                    autoHideTimeline.playFromStart();
                }
            });
            
            // Listen for mouse events on the stage
            dockStage.getScene().setOnMouseEntered(e -> {
                if (isAutoHiding) {
                    slideIn();
                }
                autoHideTimeline.stop();
            });
            
            dockStage.getScene().setOnMouseExited(e -> {
                if (isDocked && !dockStage.isFocused()) {
                    autoHideTimeline.playFromStart();
                }
            });
            
            logger.info("Auto-hide configured successfully");
            
        } catch (Exception e) {
            logger.error("Failed to setup auto-hide", e);
        }
    }

    private boolean isMouseOverStage() {
        // Simple implementation - could be enhanced with actual mouse position checking
        return dockStage.getScene().getRoot().isHover();
    }

    private void slideOut() {
        if (isAutoHiding || !isDocked) return;
        
        isAutoHiding = true;
        TranslateTransition slide = new TranslateTransition(Duration.millis(300), dockStage.getScene().getRoot());
        slide.setToX(320); // Hide most of the window, leave 40px visible
        slide.setOnFinished(e -> logger.debug("Window slid out"));
        slide.play();
    }

    private void slideIn() {
        if (!isAutoHiding) return;
        
        isAutoHiding = false;
        TranslateTransition slide = new TranslateTransition(Duration.millis(300), dockStage.getScene().getRoot());
        slide.setToX(0);
        slide.setOnFinished(e -> logger.debug("Window slid in"));
        slide.play();
        
        autoHideTimeline.stop();
    }

    private void loadClipboardEntries() {
        try {
            List<ClipboardEntry> entries = clipboardService.getAllEntries();
            
            // Clear existing entries
            entriesContainer.getChildren().clear();
            
            if (entries.isEmpty()) {
                showEmptyState();
            } else {
                hideEmptyState();
                for (ClipboardEntry entry : entries) {
                    Node entryNode = createEntryComponent(entry);
                    entriesContainer.getChildren().add(entryNode);
                }
            }
            
            logger.info("Loaded {} clipboard entries", entries.size());
            
        } catch (Exception e) {
            logger.error("Failed to load clipboard entries", e);
            showEmptyState();
        }
    }

    private Node createEntryComponent(ClipboardEntry entry) {
        return entryComponent.createEntry(entry);
    }


    private String truncateContent(String content, int maxLength) {
        return ClipboardEntryComponent.truncateContent(content, maxLength);
    }

    private String getRelativeTime(LocalDateTime dateTime) {
        return ClipboardEntryComponent.getRelativeTime(dateTime);
    }

    private void showEmptyState() {
        if (emptyState != null) {
            emptyState.setVisible(true);
            emptyState.setManaged(true);
        }
    }

    private void hideEmptyState() {
        if (emptyState != null) {
            emptyState.setVisible(false);
            emptyState.setManaged(false);
        }
    }

    @FXML
    private void clearAll() {
        try {
            clipboardService.deleteAllUnpinned();
            loadClipboardEntries();
            logger.info("Cleared all unpinned clipboard entries");
        } catch (Exception e) {
            logger.error("Failed to clear clipboard entries", e);
        }
    }

    // Public methods for external control
    public void refreshEntries() {
        Platform.runLater(this::loadClipboardEntries);
    }

    public void showDock() {
        if (dockStage != null) {
            dockStage.show();
            dockStage.toFront();
            if (isAutoHiding) {
                slideIn();
            }
        }
    }

    public void hideDock() {
        if (dockStage != null) {
            dockStage.hide();
        }
    }

    public boolean isDocked() {
        return isDocked;
    }

    public void setDocked(boolean docked) {
        this.isDocked = docked;
        if (docked) {
            // Setup auto-hide when docking
            setupAutoHide();
        } else if (autoHideTimeline != null) {
            // Stop auto-hide when undocking
            autoHideTimeline.stop();
            slideIn();
        }
    }
}