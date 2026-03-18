package com.example.collab.websocket;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * STOMP Message Controller for Real-time Collaboration
 */
@Slf4j
@Controller
public class CollabStompController {

    private final SimpMessagingTemplate messagingTemplate;
    
    // Track online users per document
    private static final Map<Long, Set<String>> documentUsers = new ConcurrentHashMap<>();
    
    // Track user cursor positions
    private static final Map<String, CursorPosition> userCursors = new ConcurrentHashMap<>();

    public CollabStompController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Handle cursor movement messages
     * Path: /app/cursor/{documentId}
     */
    @MessageMapping("/cursor/{documentId}")
    @SendTo("/topic/doc/{documentId}")
    public CursorMessage handleCursorMove(@DestinationVariable Long documentId, CursorMessage message) {
        log.debug("Cursor move: documentId={}, user={}, position={},{}", 
            documentId, message.getUserId(), message.getPosition().getX(), message.getPosition().getY());
        
        // Update cursor cache
        String key = documentId + "_" + message.getUserId();
        userCursors.put(key, message.getPosition());
        
        message.setType("CURSOR_MOVE");
        return message;
    }

    /**
     * Handle document edit messages
     * Path: /app/edit/{documentId}
     */
    @MessageMapping("/edit/{documentId}")
    @SendTo("/topic/doc/{documentId}")
    public CollabMessage handleDocumentEdit(@DestinationVariable Long documentId, CollabMessage message) {
        log.debug("Document edit: documentId={}, user={}, operation={}", 
            documentId, message.getUserId(), message.getOperation());
        
        message.setType("DOC_EDIT");
        return message;
    }

    /**
     * Handle user join
     * Path: /app/join/{documentId}
     */
    @MessageMapping("/join/{documentId}")
    public void handleUserJoin(@DestinationVariable Long documentId, CollabMessage message) {
        log.info("User join: documentId={}, user={}", documentId, message.getUserId());
        
        // Track user in document
        documentUsers.computeIfAbsent(documentId, k -> new CopyOnWriteArraySet<>())
            .add(message.getUserId());
        
        // Broadcast join event
        CollabMessage joinMessage = CollabMessage.builder()
            .type("USER_JOINED")
            .userId(message.getUserId())
            .documentId(documentId)
            .build();
        
        messagingTemplate.convertAndSend("/topic/doc/" + documentId, joinMessage);
        
        // Send current online users to the new user
        Set<String> users = documentUsers.get(documentId);
        CollabMessage userListMessage = CollabMessage.builder()
            .type("USER_LIST")
            .documentId(documentId)
            .data(Map.of("users", users))
            .build();
        messagingTemplate.convertAndSend("/topic/doc/" + documentId, userListMessage);
    }

    /**
     * Handle user leave
     * Path: /app/leave/{documentId}
     */
    @MessageMapping("/leave/{documentId}")
    public void handleUserLeave(@DestinationVariable Long documentId, CollabMessage message) {
        log.info("User leave: documentId={}, user={}", documentId, message.getUserId());
        
        // Remove user from document
        Set<String> users = documentUsers.get(documentId);
        if (users != null) {
            users.remove(message.getUserId());
            if (users.isEmpty()) {
                documentUsers.remove(documentId);
            }
        }
        
        // Clean up cursor
        String key = documentId + "_" + message.getUserId();
        userCursors.remove(key);
        
        // Broadcast leave event
        CollabMessage leaveMessage = CollabMessage.builder()
            .type("USER_LEFT")
            .userId(message.getUserId())
            .documentId(documentId)
            .build();
        
        messagingTemplate.convertAndSend("/topic/doc/" + documentId, leaveMessage);
    }

    /**
     * Handle comment events
     * Path: /app/comment/{documentId}
     */
    @MessageMapping("/comment/{documentId}")
    @SendTo("/topic/doc/{documentId}")
    public CollabMessage handleComment(@DestinationVariable Long documentId, CollabMessage message) {
        log.debug("Comment event: documentId={}, action={}", documentId, message.getOperation());
        message.setType("COMMENT_" + message.getOperation().toUpperCase());
        return message;
    }

    /**
     * Handle annotation events
     * Path: /app/annotation/{documentId}
     */
    @MessageMapping("/annotation/{documentId}")
    @SendTo("/topic/doc/{documentId}")
    public CollabMessage handleAnnotation(@DestinationVariable Long documentId, CollabMessage message) {
        log.debug("Annotation event: documentId={}, action={}", documentId, message.getOperation());
        message.setType("ANNOTATION_" + message.getOperation().toUpperCase());
        return message;
    }

    // Message DTOs
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CursorMessage {
        private String type;
        private String userId;
        private Long documentId;
        private CursorPosition position;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CursorPosition {
        private double x;
        private double y;
        private int selectionStart;
        private int selectionEnd;
        private String selectedText;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CollabMessage {
        private String type;
        private String userId;
        private Long documentId;
        private String operation;
        private Map<String, Object> data;
    }
}