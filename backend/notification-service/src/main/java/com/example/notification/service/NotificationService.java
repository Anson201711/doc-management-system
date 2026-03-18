package com.example.notification.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.notification.dto.NotificationDTO;
import com.example.notification.entity.Notification;
import com.example.notification.enums.NotificationType;
import com.example.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    
    /**
     * Send a notification to a user
     */
    @Transactional
    public NotificationDTO sendNotification(Long userId, String type, String title, String content, String sender, String link) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setSender(sender);
        notification.setLink(link);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setUpdatedAt(LocalDateTime.now());
        
        notificationRepository.insert(notification);
        return convertToDTO(notification);
    }
    
    /**
     * Get notifications for a user with pagination
     */
    public Page<NotificationDTO> getNotifications(Long userId, int page, int size) {
        Page<Notification> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId)
               .orderByDesc(Notification::getCreatedAt);
        
        Page<Notification> result = notificationRepository.selectPage(pageParam, wrapper);
        
        Page<NotificationDTO> dtoPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        dtoPage.setRecords(result.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
        return dtoPage;
    }
    
    /**
     * Get unread notification count
     */
    public long getUnreadCount(Long userId) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId)
               .eq(Notification::getRead, false);
        return notificationRepository.selectCount(wrapper);
    }
    
    /**
     * Mark a notification as read
     */
    @Transactional
    public boolean markAsRead(Long notificationId, Long userId) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getId, notificationId)
               .eq(Notification::getUserId, userId);
        
        Notification notification = notificationRepository.selectOne(wrapper);
        if (notification != null) {
            notification.setRead(true);
            notification.setUpdatedAt(LocalDateTime.now());
            notificationRepository.updateById(notification);
            return true;
        }
        return false;
    }
    
    /**
     * Mark all notifications as read for a user
     */
    @Transactional
    public int markAllAsRead(Long userId) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId)
               .eq(Notification::getRead, false);
        
        Notification notification = new Notification();
        notification.setRead(true);
        notification.setUpdatedAt(LocalDateTime.now());
        
        return notificationRepository.update(notification, wrapper);
    }
    
    /**
     * Send email notification (placeholder)
     */
    public void sendEmail(String to, String subject, String content) {
        // TODO: Implement email sending with Spring Mail
    }
    
    private NotificationDTO convertToDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setUserId(notification.getUserId());
        dto.setType(notification.getType());
        dto.setTitle(notification.getTitle());
        dto.setContent(notification.getContent());
        dto.setSender(notification.getSender());
        dto.setRead(notification.getRead());
        dto.setLink(notification.getLink());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }
}