-- 通知表
CREATE TABLE IF NOT EXISTS `t_notification` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` BIGINT NOT NULL COMMENT '接收者用户ID',
    `type` VARCHAR(30) NOT NULL COMMENT '通知类型：document_share-文档分享, comment-评论, approval-审批, system-系统通知',
    `title` VARCHAR(100) NOT NULL COMMENT '通知标题',
    `content` TEXT NOT NULL COMMENT '通知内容',
    `document_id` BIGINT NULL COMMENT '相关文档ID',
    `workflow_id` BIGINT NULL COMMENT '相关流程ID',
    `link` VARCHAR(500) NULL COMMENT '跳转链接',
    `read_status` TINYINT NOT NULL DEFAULT 0 COMMENT '是否已读：0-未读, 1-已读',
    `read_time` DATETIME NULL COMMENT '读取时间',
    `send_method` VARCHAR(20) NOT NULL DEFAULT '站内' COMMENT '发送方式：站内信-站内, email-邮件, all-全部',
    `email_status` VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '邮件发送状态：pending-待发送, sent-已发送, failed-发送失败',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_read_status` (`read_status`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知表';