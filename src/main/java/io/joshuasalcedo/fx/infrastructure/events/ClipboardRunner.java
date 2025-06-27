package io.joshuasalcedo.fx.infrastructure.events;

import io.joshuasalcedo.clipboard.core.Clipboard;
import io.joshuasalcedo.clipboard.core.ClipboardMonitor;
import io.joshuasalcedo.fx.domain.clipboard.ClipboardSavedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ClipboardRunner implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(ClipboardRunner.class);

    private final ClipboardMonitor clipboardMonitor;
    private final ApplicationEventPublisher eventPublisher;

    public ClipboardMonitor.MonitoringSession getMonitoringSession() {
        return monitoringSession;
    }

    private ClipboardMonitor.MonitoringSession monitoringSession;

    public ClipboardRunner(ClipboardMonitor clipboardMonitor, ApplicationEventPublisher eventPublisher) {
        this.clipboardMonitor = clipboardMonitor;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        start();
    }

    private void handleClipboardChange(Clipboard clipboard) {
        try {
            // Publish event for new clipboard entry
            eventPublisher.publishEvent(new ClipboardSavedEvent(this, clipboard.content()));
            logger.debug("New clipboard entry event published: {}",
                    clipboard.content().length() > 50 ?
                            clipboard.content().substring(0, 50) + "..." :
                            clipboard.content());
        } catch (Exception e) {
            logger.error("Failed to handle clipboard change", e);
        }
    }
    
    public boolean start() {
        if (monitoringSession != null && !monitoringSession.isStopped()) {
            logger.warn("Clipboard monitoring is already running");
            return true;
        }
        
        logger.info("Starting clipboard monitoring");
        this.monitoringSession = clipboardMonitor.startMonitoring(this::handleClipboardChange);
        boolean started = !this.monitoringSession.isStopped();
        
        if (started) {
            logger.info("Clipboard monitoring started successfully");
        } else {
            logger.error("Failed to start clipboard monitoring");
        }
        
        return started;
    }

    public boolean stop() {
        if (monitoringSession == null || monitoringSession.isStopped()) {
            logger.warn("Clipboard monitoring is not running");
            return true;
        }
        
        logger.info("Stopping clipboard monitoring");
        clipboardMonitor.close();
        this.monitoringSession.stop();
        boolean stopped = this.monitoringSession.isStopped();
        
        if (stopped) {
            logger.info("Clipboard monitoring stopped successfully");
        } else {
            logger.error("Failed to stop clipboard monitoring");
        }
        
        return stopped;
    }
}
