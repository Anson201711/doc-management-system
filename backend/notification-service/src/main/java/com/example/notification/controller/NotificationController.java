package com.example.notification.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.notification.dto.NotificationDTO;
import com.example.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {
    
    private final NotificationService notificationService;
    
    /**
     * Get notifications for current user
     * GET /api/v1/notifications?userId=1&page=1&size=10
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getNotifications(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Page<NotificationDTO> notifications = notificationService.getNotifications(userId, page, size);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "获取成功");
        response.put("data", notifications);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get unread notification count
     * GET /api/v1/notifications/unread?userId=1
     */
    @GetMapping("/unread")
    public ResponseEntity<Map<String, Object>> getUnreadCount(@RequestParam Long userId) {
        long count = notificationService.getUnreadCount(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "获取成功");
        response.put("data", count);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Mark a notification as read
     * PUT /api/v1/notifications/{id}/read?userId=1
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<Map<String, Object>> markAsRead(
            @PathVariable Long id,
            @RequestParam Long userId) {
        
        boolean success = notificationService.markAsRead(id, userId);
        
        Map<String, Object> response = new HashMap<>();
        if (success) {
            response.put("code", 200);
            response.put("message", "标记已读成功");
        } else {
            response.put("code", 404);
            response.put("message", "通知不存在");
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Mark all notifications as read
     * PUT /api/v1/notifications/read-all?userId=1
     */
    @PutMapping("/read-all")
    public ResponseEntity<Map<String, Object>> markAllAsRead(@RequestParam Long userId) {
        int count = notificationService.markAllAsRead(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "全部标记已读成功");
        response.put("data", count);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Send a notification (internal use)
     * POST /api/v1/notifications/send
     */
    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendNotification(
            @RequestParam Long userId,
            @RequestParam String type,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam(required = false, defaultValue = "System") String sender,
            @RequestParam(required = false) String link) {
        
        NotificationDTO notification = notificationService.sendNotification(userId, type, title, content, sender, link);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "通知发送成功");
        response.put("data", notification);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Send email notification
     * POST /api/v1/notifications/email
     */
    @PostMapping("/email")
    public ResponseEntity<Map<String, Object>> sendEmail(
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String content) {
        
        notificationService.sendEmail(to, subject, content);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "邮件发送成功");
        
        return ResponseEntity.ok(response);
    }
}