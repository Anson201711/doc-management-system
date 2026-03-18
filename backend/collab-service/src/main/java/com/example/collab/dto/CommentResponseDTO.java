package com.example.collab.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Comment Response DTO
 */
@Data
public class CommentResponseDTO {

    private Long id;

    private Long documentId;

    private Long userId;

    private Long parentId;

    private String content;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}