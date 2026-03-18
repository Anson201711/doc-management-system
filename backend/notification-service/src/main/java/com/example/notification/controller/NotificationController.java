package com.example.notification.controller;

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
    
    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendNotification(
            @RequestParam Long userId,
            @RequestParam String message) {
        notificationService.sendNotification(userId, message);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "通知发送成功");
        return ResponseEntity.ok(response);
    }
    
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