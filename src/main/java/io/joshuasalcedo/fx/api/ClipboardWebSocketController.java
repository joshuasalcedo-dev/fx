package io.joshuasalcedo.fx.api;

import io.joshuasalcedo.fx.domain.clipboard.ClipboardEntry;
import io.joshuasalcedo.fx.presentation.dto.ClipboardDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ClipboardWebSocketController {
    private static final Logger log = LoggerFactory.getLogger(ClipboardWebSocketController.class);
    
    private final SimpMessagingTemplate messagingTemplate;
    
    public ClipboardWebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
    
    /**
     * Broadcasts a new clipboard entry to all connected clients
     */
    public void broadcastNewEntry(ClipboardEntry entry) {
        log.debug("Broadcasting new clipboard entry to WebSocket clients");
        
        ClipboardDto dto = new ClipboardDto(
                entry.getId(),
                entry.getTimestamp(),
                entry.getContent(),
                entry.isPinned()
        );
        
        messagingTemplate.convertAndSend("/topic/clipboard/new", dto);
    }
    
    /**
     * Broadcasts when an entry is updated (e.g., pinned/unpinned)
     */
    public void broadcastUpdate(ClipboardEntry entry) {
        log.debug("Broadcasting clipboard update for entry ID: {}", entry.getId());
        
        ClipboardDto dto = new ClipboardDto(
                entry.getId(),
                entry.getTimestamp(),
                entry.getContent(),
                entry.isPinned()
        );
        
        messagingTemplate.convertAndSend("/topic/clipboard/update", dto);
    }
    
    /**
     * Broadcasts when an entry is deleted
     */
    public void broadcastDelete(Long entryId) {
        log.debug("Broadcasting clipboard deletion for entry ID: {}", entryId);
        messagingTemplate.convertAndSend("/topic/clipboard/delete", entryId);
    }
    
    /**
     * Broadcasts when all entries are cleared
     */
    public void broadcastClear(boolean includePinned) {
        log.debug("Broadcasting clipboard clear event - includePinned: {}", includePinned);
        messagingTemplate.convertAndSend("/topic/clipboard/clear", 
                new ClearEvent(includePinned));
    }
    
    /**
     * Handles ping messages to keep WebSocket connection alive
     */
    @MessageMapping("/clipboard/ping")
    @SendTo("/topic/clipboard/pong")
    public String handlePing(String message) {
        return "pong";
    }
    
    // Event classes
    public record ClearEvent(boolean includePinned) {}
}