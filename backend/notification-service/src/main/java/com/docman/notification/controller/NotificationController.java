package com.docman.notification.controller;

import com.docman.notification.dto.NotificationCreateDTO;
import com.docman.notification.dto.NotificationDTO;
import com.docman.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 通知管理控制器
 */
@Tag(name = "通知管理")
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "发送通知")
    @PostMapping
    public NotificationDTO send(@Valid @RequestBody NotificationCreateDTO dto) {
        return notificationService.send(dto);
    }

    @Operation(summary = "批量发送通知")
    @PostMapping("/batch")
    public List<NotificationDTO> batchSend(@RequestBody List<NotificationCreateDTO> dtos) {
        return notificationService.batchSend(dtos);
    }

    @Operation(summary = "获取用户通知列表")
    @GetMapping
    public List<NotificationDTO> list(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false) String readStatus,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return notificationService.listByUserId(userId, readStatus, page, size);
    }

    @Operation(summary = "获取未读通知数量")
    @GetMapping("/unread-count")
    public Map<String, Integer> getUnreadCount(@RequestHeader("X-User-Id") Long userId) {
        int count = notificationService.getUnreadCount(userId);
        return Map.of("count", count);
    }

    @Operation(summary = "标记通知为已读")
    @PutMapping("/{id}/read")
    public void markAsRead(@PathVariable Long id,
                            @RequestHeader("X-User-Id") Long userId) {
        notificationService.markAsRead(id, userId);
    }

    @Operation(summary = "标记所有通知为已读")
    @PutMapping("/read-all")
    public void markAllAsRead(@RequestHeader("X-User-Id") Long userId) {
        notificationService.markAllAsRead(userId);
    }

    @Operation(summary = "删除通知")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id,
                        @RequestHeader("X-User-Id") Long userId) {
        notificationService.delete(id, userId);
    }
}