package com.docman.document.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文档评论实体
 */
@Data
@TableName("t_document_comment")
public class DocumentComment {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文档ID
     */
    private Long documentId;

    /**
     * 父评论ID（用于回复）
     */
    private Long parentId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 评论类型：comment-普通评论, annotation-批注
     */
    private String commentType;

    /**
     * 批注位置（起始位置）
     */
    private Integer startPosition;

    /**
     * 批注位置（结束位置）
     */
    private Integer endPosition;

    /**
     * 选中的文本
     */
    private String selectedText;

    /**
     * 评论者ID
     */
    private Long creatorId;

    /**
     * 评论者名称
     */
    private String creatorName;

    /**
     * 状态：active-正常, deleted-已删除
     */
    @TableLogic
    private Integer deleted;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}