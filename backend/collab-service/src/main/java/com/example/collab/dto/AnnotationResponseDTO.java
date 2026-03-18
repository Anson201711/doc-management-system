package com.example.collab.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Annotation Response DTO
 */
@Data
public class AnnotationResponseDTO {

    private Long id;

    private Long documentId;

    private Long userId;

    private Long versionId;

    private Integer pageNumber;

    private String position;

    private String content;

    private String color;

    private Long creatorId;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}