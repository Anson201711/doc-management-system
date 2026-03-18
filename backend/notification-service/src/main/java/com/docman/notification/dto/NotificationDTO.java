package com.docman.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通知响应
 */
@Data
@Schema(description = "通知响应")
public class NotificationDTO {

    private Long id;
    private Long userId;
    private String type;
    private String title;
    private String content;
    private Long documentId;
    private Long workflowId;
    private String link;
    private Integer readStatus;
    private LocalDateTime readTime;
    private String sendMethod;
    private LocalDateTime createdAt;
}