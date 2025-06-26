package io.joshuasalcedo.fx.infrastructure.clipboard;

import io.joshuasalcedo.clipboard.core.Clipboard;
import io.joshuasalcedo.clipboard.core.ClipboardMonitor;
import io.joshuasalcedo.fx.application.clipboard.ClipboardDockController;
import io.joshuasalcedo.fx.domain.clipboard.ClipboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ClipboardRunner implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(ClipboardRunner.class);
    
    private final ClipboardMonitor clipboardMonitor;
    private final ClipboardService clipboardService;
    
    @Autowired
    private ApplicationContext applicationContext;
    
    public ClipboardRunner(ClipboardMonitor clipboardMonitor, ClipboardService clipboardService) {
        this.clipboardMonitor = clipboardMonitor;
        this.clipboardService = clipboardService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        clipboardMonitor.startMonitoring(this::handleClipboardChange);
        logger.info("Clipboard monitoring started");
    }

    private void handleClipboardChange(Clipboard clipboard) {
        try {
            // Save the new clipboard entry
            clipboardService.save(clipboard.content());
            logger.debug("New clipboard entry saved: {}", 
                        clipboard.content().length() > 50 ? 
                        clipboard.content().substring(0, 50) + "..." : 
                        clipboard.content());
            
            // Refresh the UI if the dock controller is available
            refreshClipboardDockUI();
            
        } catch (Exception e) {
            logger.error("Failed to handle clipboard change", e);
        }
    }

    private void refreshClipboardDockUI() {
        try {
            ClipboardDockController controller = applicationContext.getBean(ClipboardDockController.class);
            controller.refreshEntries();
        } catch (Exception e) {
            // Controller might not be initialized yet, which is fine
            logger.debug("Could not refresh clipboard dock UI: {}", e.getMessage());
        }
    }
}
