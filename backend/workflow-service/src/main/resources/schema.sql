-- 工作流服务数据库初始化脚本

-- 工作流表
CREATE TABLE IF NOT EXISTS workflows (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    document_id BIGINT NOT NULL COMMENT '文档ID',
    creator_id BIGINT NOT NULL COMMENT '创建人ID',
    approver_id BIGINT NOT NULL COMMENT '审批人ID',
    title VARCHAR(255) NOT NULL COMMENT '工作流标题',
    description VARCHAR(1000) COMMENT '工作流描述',
    current_status VARCHAR(20) DEFAULT 'pending' COMMENT '当前状态: pending(待审批), approved(已通过), rejected(已拒绝)',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    completed_at DATETIME COMMENT '完成时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除标记: 0-未删除, 1-已删除',
    INDEX idx_document_id (document_id),
    INDEX idx_creator_id (creator_id),
    INDEX idx_approver_id (approver_id),
    INDEX idx_current_status (current_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工作流表';

-- 工作流日志表
CREATE TABLE IF NOT EXISTS workflow_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    workflow_id BIGINT NOT NULL COMMENT '工作流ID',
    approver_id BIGINT NOT NULL COMMENT '审批人ID',
    action VARCHAR(20) NOT NULL COMMENT '操作: approve(通过), reject(拒绝)',
    comment VARCHAR(1000) COMMENT '审批意见',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除标记: 0-未删除, 1-已删除',
    INDEX idx_workflow_id (workflow_id),
    INDEX idx_approver_id (approver_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工作流日志表';

-- 插入测试数据
INSERT INTO workflows (document_id, creator_id, approver_id, title, description, current_status) VALUES
(1, 1, 2, '文档审批测试', '测试文档需要审批', 'pending');

INSERT INTO workflow_logs (workflow_id, approver_id, action, comment) VALUES
(1, 2, 'approve', '审批通过');