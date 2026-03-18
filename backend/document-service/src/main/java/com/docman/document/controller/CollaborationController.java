package com.docman.document.controller;

import com.docman.document.service.CollaborationService;
import com.docman.document.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * WebSocket协作控制器
 */
@Tag(name = "实时协作")
@RestController
@RequiredArgsConstructor
public class CollaborationController {

    private final CollaborationService collaborationService;

    @Operation(summary = "获取文档在线用户")
    @GetMapping("/api/v1/documents/{docId}/collaboration/users")
    public Result<List<Map<String, Object>>> getOnlineUsers(@PathVariable Long docId) {
        List<Map<String, Object>> users = collaborationService.getOnlineUsers(docId);
        return Result.success(users);
    }

    @Operation(summary = "获取操作历史")
    @GetMapping("/api/v1/documents/{docId}/collaboration/history")
    public Result<List<CollaborationService.OperationLog>> getOperationHistory(
            @PathVariable Long docId,
            @RequestParam(defaultValue = "100") int limit) {
        List<CollaborationService.OperationLog> history = 
                collaborationService.getOperationHistory(docId, limit);
        return Result.success(history);
    }

    /**
     * 处理加入文档编辑
     */
    @MessageMapping("/document/{docId}/join")
    @SendTo("/topic/document/{docId}")
    public Map<String, Object> handleJoin(
            @DestinationVariable Long docId,
            @Payload Map<String, Object> payload,
            SimpMessageHeaderAccessor headerAccessor) {
        
        Long userId = Long.valueOf(payload.get("userId").toString());
        String userName = payload.get("userName").toString();
        
        collaborationService.userJoined(docId, userId, userName);
        
        return Map.of(
                "type", "USER_JOINED",
                "docId", docId,
                "userId", userId,
                "userName", userName
        );
    }

    /**
     * 处理离开文档编辑
     */
    @MessageMapping("/document/{docId}/leave")
    public void handleLeave(
            @DestinationVariable Long docId,
            @Payload Map<String, Object> payload) {
        
        Long userId = Long.valueOf(payload.get("userId").toString());
        collaborationService.userLeft(docId, userId);
    }

    /**
     * 处理文档编辑操作
     */
    @MessageMapping("/document/{docId}/edit")
    public void handleEdit(
            @DestinationVariable Long docId,
            @Payload Map<String, Object> payload) {
        
        Long userId = Long.valueOf(payload.get("userId").toString());
        String operation = payload.get("operation").toString();
        
        collaborationService.handleEditOperation(docId, userId, operation);
    }

    /**
     * 处理光标位置更新
     */
    @MessageMapping("/document/{docId}/cursor")
    public void handleCursorUpdate(
            @DestinationVariable Long docId,
            @Payload Map<String, Object> payload) {
        
        Long userId = Long.valueOf(payload.get("userId").toString());
        Integer position = Integer.valueOf(payload.get("position").toString());
        String selection = payload.get("selection") != null ? 
                payload.get("selection").toString() : null;
        
        collaborationService.updateCursorPosition(docId, userId, position, selection);
    }
}