package com.example.collab.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Annotation Create DTO
 */
@Data
public class AnnotationCreateDTO {

    @NotNull(message = "Document ID cannot be null")
    private Long documentId;

    @NotNull(message = "User ID cannot be null")
    private Long userId;

    private Long versionId;

    private Integer pageNumber;

    @NotBlank(message = "Position cannot be blank")
    private String position;

    @NotBlank(message = "Content cannot be blank")
    private String content;

    private String color;

    private Long creatorId;

    private String status = "active";
}