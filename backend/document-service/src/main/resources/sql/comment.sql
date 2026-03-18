-- 评论表
CREATE TABLE IF NOT EXISTS `t_document_comment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `document_id` BIGINT NOT NULL COMMENT '文档ID',
    `parent_id` BIGINT NULL COMMENT '父评论ID',
    `content` TEXT NOT NULL COMMENT '评论内容',
    `comment_type` VARCHAR(20) NOT NULL DEFAULT 'comment' COMMENT '评论类型：comment-普通评论, annotation-批注',
    `start_position` INT NULL COMMENT '批注起始位置',
    `end_position` INT NULL COMMENT '批注结束位置',
    `selected_text` VARCHAR(500) NULL COMMENT '选中的文本',
    `creator_id` BIGINT NOT NULL COMMENT '评论者ID',
    `creator_name` VARCHAR(50) NULL COMMENT '评论者名称',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-正常, 1-删除',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_document_id` (`document_id`),
    INDEX `idx_parent_id` (`parent_id`),
    INDEX `idx_creator_id` (`creator_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文档评论表';