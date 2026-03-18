package com.example.collab.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Comment Create DTO
 */
@Data
public class CommentCreateDTO {

    @NotNull(message = "Document ID cannot be null")
    private Long documentId;

    @NotNull(message = "User ID cannot be null")
    private Long userId;

    private Long parentId;

    @NotBlank(message = "Content cannot be blank")
    private String content;

    private String status = "active";
}