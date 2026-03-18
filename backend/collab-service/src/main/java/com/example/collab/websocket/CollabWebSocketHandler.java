package com.example.collab.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Collaboration WebSocket Handler
 */
@Slf4j
@Component
@ServerEndpoint("/ws/collab/{documentId}")
public class CollabWebSocketHandler {

    private static final Map<Long, Set<Session>> documentSessions = new ConcurrentHashMap<>();
    private static ObjectMapper objectMapper = new ObjectMapper();

    @OnOpen
    public void onOpen(Session session, @PathParam("documentId") Long documentId) {
        session.getUserProperties().put("documentId", documentId);
        session.getUserProperties().put("sessionId", session.getId());
        
        documentSessions.computeIfAbsent(documentId, k -> new CopyOnWriteArraySet<>()).add(session);
        log.info("WebSocket connected: sessionId={}, documentId={}", session.getId(), documentId);
        
        // Broadcast user joined
        broadcastToDocument(documentId, "USER_JOINED", Map.of(
            "sessionId", session.getId(),
            "documentId", documentId
        ));
    }

    @OnClose
    public void onClose(Session session, @PathParam("documentId") Long documentId) {
        Long docId = (Long) session.getUserProperties().get("documentId");
        if (docId != null) {
            Set<Session> sessions = documentSessions.get(docId);
            if (sessions != null) {
                sessions.remove(session);
                if (sessions.isEmpty()) {
                    documentSessions.remove(docId);
                }
            }
        }
        log.info("WebSocket closed: sessionId={}, documentId={}", session.getId(), documentId);
        
        // Broadcast user left
        broadcastToDocument(documentId, "USER_LEFT", Map.of(
            "sessionId", session.getId(),
            "documentId", documentId
        ));
    }

    @OnError
    public void onError(Session session, Throwable error, @PathParam("documentId") Long documentId) {
        log.error("WebSocket error: sessionId={}, documentId={}, error={}", 
            session.getId(), documentId, error.getMessage());
    }

    @OnMessage
    public void onMessage(String message, Session session, @PathParam("documentId") Long documentId) {
        try {
            Map<String, Object> msgMap = objectMapper.readValue(message, Map.class);
            String type = (String) msgMap.get("type");
            
            log.debug("Received message: type={}, documentId={}", type, documentId);
            
            switch (type) {
                case "COMMENT_ADDED":
                case "COMMENT_UPDATED":
                case "COMMENT_DELETED":
                    broadcastToDocument(documentId, type, msgMap.get("data"));
                    break;
                case "ANNOTATION_ADDED":
                case "ANNOTATION_UPDATED":
                case "ANNOTATION_DELETED":
                    broadcastToDocument(documentId, type, msgMap.get("data"));
                    break;
                case "CURSOR_MOVE":
                    broadcastToOthers(session, documentId, type, msgMap.get("data"));
                    break;
                default:
                    log.warn("Unknown message type: {}", type);
            }
        } catch (IOException e) {
            log.error("Failed to parse message: {}", e.getMessage());
        }
    }

    /**
     * Broadcast message to all sessions in a document
     */
    public static void broadcastToDocument(Long documentId, String type, Object data) {
        Set<Session> sessions = documentSessions.get(documentId);
        if (sessions == null || sessions.isEmpty()) return;
        
        try {
            String message = objectMapper.writeValueAsString(Map.of(
                "type", type,
                "data", data
            ));
            
            for (Session session : sessions) {
                if (session.isOpen()) {
                    session.getBasicRemote().sendText(message);
                }
            }
        } catch (IOException e) {
            log.error("Failed to broadcast message: {}", e.getMessage());
        }
    }

    /**
     * Broadcast to other sessions except sender
     */
    private void broadcastToOthers(Session exclude, Long documentId, String type, Object data) {
        Set<Session> sessions = documentSessions.get(documentId);
        if (sessions == null || sessions.isEmpty()) return;
        
        try {
            String message = objectMapper.writeValueAsString(Map.of(
                "type", type,
                "data", data
            ));
            
            for (Session session : sessions) {
                if (session.isOpen() && !session.getId().equals(exclude.getId())) {
                    session.getBasicRemote().sendText(message);
                }
            }
        } catch (IOException e) {
            log.error("Failed to broadcast message: {}", e.getMessage());
        }
    }

    /**
     * Get active session count for a document
     */
    public static int getActiveSessionCount(Long documentId) {
        Set<Session> sessions = documentSessions.get(documentId);
        return sessions != null ? sessions.size() : 0;
    }
}