package com.docman.notification.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.docman.notification.dto.NotificationCreateDTO;
import com.docman.notification.dto.NotificationDTO;
import com.docman.notification.entity.Notification;
import com.docman.notification.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 通知服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService extends ServiceImpl<NotificationMapper, Notification> {

    private final SimpMessagingTemplate messagingTemplate;
    private final JavaMailSender mailSender;

    @javax.annotation.Value("${spring.mail.username:}")
    private String mailFrom;

    /**
     * 发送通知
     */
    @Transactional
    public NotificationDTO send(NotificationCreateDTO dto) {
        Notification notification = new Notification();
        notification.setUserId(dto.getUserId());
        notification.setType(dto.getType());
        notification.setTitle(dto.getTitle());
        notification.setContent(dto.getContent());
        notification.setDocumentId(dto.getDocumentId());
        notification.setWorkflowId(dto.getWorkflowId());
        notification.setLink(dto.getLink());
        notification.setSendMethod(dto.getSendMethod() != null ? dto.getSendMethod() : "站内");
        notification.setReadStatus(0);
        notification.setEmailStatus("pending");
        notification.setCreatedAt(LocalDateTime.now());
        notification.setUpdatedAt(LocalDateTime.now());

        this.save(notification);

        // 站内信实时推送
        if ("站内".equals(dto.getSendMethod()) || "all".equals(dto.getSendMethod())) {
            pushToUser(notification);
        }

        // 邮件发送
        if ("邮件".equals(dto.getSendMethod()) || "all".equals(dto.getSendMethod())) {
            sendEmail(dto);
        }

        log.info("发送通知成功: 用户={}, 类型={}", dto.getUserId(), dto.getType());
        return toDTO(notification);
    }

    /**
     * 批量发送通知
     */
    @Transactional
    public List<NotificationDTO> batchSend(List<NotificationCreateDTO> dtos) {
        List<Notification> notifications = dtos.stream()
                .map(dto -> {
                    Notification n = new Notification();
                    n.setUserId(dto.getUserId());
                    n.setType(dto.getType());
                    n.setTitle(dto.getTitle());
                    n.setContent(dto.getContent());
                    n.setDocumentId(dto.getDocumentId());
                    n.setLink(dto.getLink());
                    n.setSendMethod(dto.getSendMethod() != null ? dto.getSendMethod() : "站内");
                    n.setReadStatus(0);
                    n.setEmailStatus("pending");
                    n.setCreatedAt(LocalDateTime.now());
                    n.setUpdatedAt(LocalDateTime.now());
                    return n;
                })
                .collect(Collectors.toList());

        this.saveBatch(notifications);

        // 实时推送
        notifications.forEach(this::pushToUser);

        log.info("批量发送通知成功: 数量={}", notifications.size());
        return notifications.stream().map(this::toDTO).collect(Collectors.toList());
    }

    /**
     * 获取用户通知列表
     */
    public List<NotificationDTO> listByUserId(Long userId, String readStatus, int page, int size) {
        Page<Notification> pageParam = new Page<>(page, size);
        
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId);
        if (readStatus != null) {
            wrapper.eq(Notification::getReadStatus, "1".equals(readStatus) ? 1 : 0);
        }
        wrapper.orderByDesc(Notification::getCreatedAt);

        IPage<Notification> pageResult = this.page(pageParam, wrapper);
        return pageResult.getRecords().stream().map(this::toDTO).collect(Collectors.toList());
    }

    /**
     * 获取用户未读通知数量
     */
    public int getUnreadCount(Long userId) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId);
        wrapper.eq(Notification::getReadStatus, 0);
        return this.count(wrapper);
    }

    /**
     * 标记通知为已读
     */
    @Transactional
    public void markAsRead(Long id, Long userId) {
        Notification notification = this.getById(id);
        if (notification != null && notification.getUserId().equals(userId)) {
            notification.setReadStatus(1);
            notification.setReadTime(LocalDateTime.now());
            notification.setUpdatedAt(LocalDateTime.now());
            this.updateById(notification);
        }
    }

    /**
     * 标记所有通知为已读
     */
    @Transactional
    public void markAllAsRead(Long userId) {
        Notification notification = new Notification();
        notification.setReadStatus(1);
        notification.setReadTime(LocalDateTime.now());
        
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId);
        wrapper.eq(Notification::getReadStatus, 0);
        
        this.update(notification, wrapper);
    }

    /**
     * 删除通知
     */
    public void delete(Long id, Long userId) {
        Notification notification = this.getById(id);
        if (notification != null && notification.getUserId().equals(userId)) {
            this.removeById(id);
        }
    }

    /**
     * WebSocket推送站内信
     */
    private void pushToUser(Notification notification) {
        try {
            messagingTemplate.convertAndSendToUser(
                    notification.getUserId().toString(),
                    "/queue/notifications",
                    toDTO(notification)
            );
        } catch (Exception e) {
            log.warn("WebSocket推送失败: {}", e.getMessage());
        }
    }

    /**
     * 发送邮件通知
     */
    private void sendEmail(NotificationCreateDTO dto) {
        // 实际项目中需要从用户服务获取邮箱
        // 这里简化处理
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailFrom);
            message.setTo(dto.getEmail() != null ? dto.getEmail() : "user@example.com");
            message.setSubject(dto.getTitle());
            message.setText(dto.getContent());
            mailSender.send(message);
            
            log.info("邮件发送成功: {}", dto.getTitle());
        } catch (Exception e) {
            log.error("邮件发送失败: {}", e.getMessage());
        }
    }

    private NotificationDTO toDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setUserId(notification.getUserId());
        dto.setType(notification.getType());
        dto.setTitle(notification.getTitle());
        dto.setContent(notification.getContent());
        dto.setDocumentId(notification.getDocumentId());
        dto.setWorkflowId(notification.getWorkflowId());
        dto.setLink(notification.getLink());
        dto.setReadStatus(notification.getReadStatus());
        dto.setReadTime(notification.getReadTime());
        dto.setSendMethod(notification.getSendMethod());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }
}