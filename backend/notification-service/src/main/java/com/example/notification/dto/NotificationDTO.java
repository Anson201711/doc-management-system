package com.example.notification.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationDTO {
    private Long id;
    private Long userId;
    private String type;
    private String title;
    private String content;
    private String sender;
    private Boolean read;
    private String link;
    private LocalDateTime createdAt;
}