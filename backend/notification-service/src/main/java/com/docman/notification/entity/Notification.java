package com.docman.notification.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通知实体
 */
@Data
@TableName("t_notification")
public class Notification {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 接收者用户ID
     */
    private Long userId;

    /**
     * 通知类型：document_share-文档分享, comment-评论, approval-审批, system-系统通知
     */
    private String type;

    /**
     * 通知标题
     */
    private String title;

    /**
     * 通知内容
     */
    private String content;

    /**
     * 相关文档ID
     */
    private Long documentId;

    /**
     * 相关流程ID
     */
    private Long workflowId;

    /**
     * 跳转链接
     */
    private String link;

    /**
     * 是否已读：0-未读, 1-已读
     */
    private Integer readStatus;

    /**
     * 读取时间
     */
    private LocalDateTime readTime;

    /**
     * 发送方式：站内信-站内, email-邮件, all-全部
     */
    private String sendMethod;

    /**
     * 邮件发送状态：pending-待发送, sent-已发送, failed-发送失败
     */
    private String emailStatus;

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