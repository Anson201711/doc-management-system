package com.docman.document.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 评论响应
 */
@Data
@Schema(description = "评论响应")
public class CommentVO {

    private Long id;
    private Long documentId;
    private Long parentId;
    private String content;
    private String commentType;
    private Integer startPosition;
    private Integer endPosition;
    private String selectedText;
    private Long creatorId;
    private String creatorName;
    private String createdAt;
    private String updatedAt;
    private Integer replyCount;
}