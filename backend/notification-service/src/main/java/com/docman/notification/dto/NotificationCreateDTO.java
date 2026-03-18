package com.docman.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 通知创建请求
 */
@Data
@Schema(description = "通知创建请求")
public class NotificationCreateDTO {

    @Schema(description = "接收者用户ID")
    @NotNull
    private Long userId;

    @Schema(description = "通知类型")
    @NotNull
    private String type;

    @Schema(description = "通知标题")
    @NotNull
    private String title;

    @Schema(description = "通知内容")
    @NotNull
    private String content;

    @Schema(description = "相关文档ID")
    private Long documentId;

    @Schema(description = "相关流程ID")
    private Long workflowId;

    @Schema(description = "跳转链接")
    private String link;

    @Schema(description = "发送方式：站内/邮件/all")
    private String sendMethod;

    @Schema(description = "邮箱地址")
    private String email;
}