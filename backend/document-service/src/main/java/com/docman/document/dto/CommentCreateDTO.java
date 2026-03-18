package com.docman.document.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建评论请求
 */
@Data
@Schema(description = "创建评论请求")
public class CommentCreateDTO {

    @Schema(description = "文档ID")
    @NotNull(message = "文档ID不能为空")
    private Long documentId;

    @Schema(description = "父评论ID（用于回复）")
    private Long parentId;

    @Schema(description = "评论内容")
    @NotBlank(message = "评论内容不能为空")
    private String content;

    @Schema(description = "评论类型：comment-普通评论, annotation-批注")
    private String commentType = "comment";

    @Schema(description = "批注位置（起始位置）")
    private Integer startPosition;

    @Schema(description = "批注位置（结束位置）")
    private Integer endPosition;

    @Schema(description = "选中的文本")
    private String selectedText;
}