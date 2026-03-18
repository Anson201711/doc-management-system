package com.docman.document.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * WebSocket实时协作服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CollaborationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    // 文档在线用户: docId -> Set<SessionInfo>
    private final ConcurrentHashMap<Long, Set<SessionInfo>> documentSessions = new ConcurrentHashMap<>();
    
    // 用户编辑锁: docId -> userId
    private final ConcurrentHashMap<String, String> editLocks = new ConcurrentHashMap<>();
    
    // 操作历史: docId -> Queue
    private final ConcurrentHashMap<Long, Queue<OperationLog>> operationHistory = new ConcurrentHashMap<>();

    /**
     * 用户加入文档编辑
     */
    public void userJoined(Long docId, Long userId, String userName) {
        SessionInfo session = new SessionInfo();
        session.setUserId(userId);
        session.setUserName(userName);
        session.setJoinTime(LocalDateTime.now());
        
        documentSessions.computeIfAbsent(docId, k -> Collections.synchronizedSet(new HashSet<>()));
        documentSessions.get(docId).add(session);
        
        // 广播用户加入
        Map<String, Object> message = new HashMap<>();
        message.put("type", "USER_JOINED");
        message.put("docId", docId);
        message.put("userId", userId);
        message.put("userName", userName);
        message.put("onlineUsers", getOnlineUsers(docId));
        
        broadcastToDocument(docId, message);
        log.info("用户 {} 加入文档 {} 编辑", userName, docId);
    }

    /**
     * 用户离开文档编辑
     */
    public void userLeft(Long docId, Long userId) {
        Set<SessionInfo> sessions = documentSessions.get(docId);
        if (sessions != null) {
            sessions.removeIf(s -> s.getUserId().equals(userId));
            if (sessions.isEmpty()) {
                documentSessions.remove(docId);
            }
        }
        
        // 释放编辑锁
        editLocks.remove(docId + ":" + userId);
        
        // 广播用户离开
        Map<String, Object> message = new HashMap<>();
        message.put("type", "USER_LEFT");
        message.put("docId", docId);
        message.put("userId", userId);
        message.put("onlineUsers", getOnlineUsers(docId));
        
        broadcastToDocument(docId, message);
        log.info("用户 {} 离开文档 {} 编辑", userId, docId);
    }

    /**
     * 处理文档编辑操作
     */
    public void handleEditOperation(Long docId, Long userId, String operation) {
        String lockKey = docId + ":" + userId;
        
        try {
            // 检查并获取编辑锁
            String existingLock = editLocks.putIfAbsent(lockKey, lockKey);
            if (existingLock != null) {
                // 发送锁冲突消息
                Map<String, Object> errorMsg = new HashMap<>();
                errorMsg.put("type", "LOCK_CONFLICT");
                errorMsg.put("docId", docId);
                errorMsg.put("userId", userId);
                errorMsg.put("message", "文档正在被其他用户编辑");
                messagingTemplate.convertAndSendToUser(userId.toString(), "/queue/errors", errorMsg);
                return;
            }
            
            // 记录操作
            recordOperation(docId, userId, operation);
            
            // 广播操作给其他用户
            Map<String, Object> message = new HashMap<>();
            message.put("type", "DOCUMENT_EDIT");
            message.put("docId", docId);
            message.put("userId", userId);
            message.put("operation", operation);
            message.put("timestamp", System.currentTimeMillis());
            
            broadcastToOthers(docId, userId, message);
            
        } finally {
            editLocks.remove(lockKey);
        }
    }

    /**
     * 处理光标位置更新
     */
    public void updateCursorPosition(Long docId, Long userId, Integer position, String selection) {
        Map<String, Object> message = new HashMap<>();
        message.put("type", "CURSOR_UPDATE");
        message.put("docId", docId);
        message.put("userId", userId);
        message.put("position", position);
        message.put("selection", selection);
        
        broadcastToOthers(docId, userId, message);
    }

    /**
     * 获取文档在线用户列表
     */
    public List<Map<String, Object>> getOnlineUsers(Long docId) {
        Set<SessionInfo> sessions = documentSessions.get(docId);
        if (sessions == null) {
            return Collections.emptyList();
        }
        
        return sessions.stream()
                .map(s -> {
                    Map<String, Object> user = new HashMap<>();
                    user.put("userId", s.getUserId());
                    user.put("userName", s.getUserName());
                    user.put("joinTime", s.getJoinTime());
                    return user;
                })
                .toList();
    }

    /**
     * 广播消息给文档所有用户
     */
    private void broadcastToDocument(Long docId, Map<String, Object> message) {
        messagingTemplate.convertAndSend("/topic/document/" + docId, message);
    }

    /**
     * 广播消息给除指定用户外的所有用户
     */
    private void broadcastToOthers(Long docId, Long excludeUserId, Map<String, Object> message) {
        Set<SessionInfo> sessions = documentSessions.get(docId);
        if (sessions == null) return;
        
        for (SessionInfo session : sessions) {
            if (!session.getUserId().equals(excludeUserId)) {
                messagingTemplate.convertAndSendToUser(
                        session.getUserId().toString(), 
                        "/queue/document", 
                        message
                );
            }
        }
    }

    /**
     * 记录操作历史
     */
    private void recordOperation(Long docId, Long userId, String operation) {
        Queue<OperationLog> history = operationHistory.computeIfAbsent(
                docId, k -> new ConcurrentLinkedQueue<>()
        );
        
        OperationLog log = new OperationLog();
        log.setUserId(userId);
        log.setOperation(operation);
        log.setTimestamp(System.currentTimeMillis());
        
        history.offer(log);
        
        // 保留最近1000条操作
        while (history.size() > 1000) {
            history.poll();
        }
    }

    /**
     * 获取操作历史
     */
    public List<OperationLog> getOperationHistory(Long docId, int limit) {
        Queue<OperationLog> history = operationHistory.get(docId);
        if (history == null) {
            return Collections.emptyList();
        }
        
        return history.stream()
                .limit(limit)
                .toList();
    }

    /**
     * 内部类：会话信息
     */
    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class SessionInfo {
        private Long userId;
        private String userName;
        private LocalDateTime joinTime;
    }

    /**
     * 内部类：操作日志
     */
    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class OperationLog {
        private Long userId;
        private String operation;
        private long timestamp;
    }
}