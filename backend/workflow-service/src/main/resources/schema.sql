-- 工作流服务数据库表结构
-- 创建时间: 2026-03-18

-- 创建工作流表 (t_workflow)
CREATE TABLE IF NOT EXISTS t_workflow (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    workflow_type VARCHAR(50) NOT NULL COMMENT '工作流类型',
    document_id BIGINT NOT NULL COMMENT '文档ID',
    initiator_id BIGINT NOT NULL COMMENT '发起人ID',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态: PENDING/APPROVING/APPROVED/REJECTED',
    current_level INT DEFAULT 1 COMMENT '当前审批级别',
    total_levels INT DEFAULT 1 COMMENT '总审批级别数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    INDEX idx_document (document_id),
    INDEX idx_initiator (initiator_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工作流表';

-- 创建审批任务表 (t_approval_task)
CREATE TABLE IF NOT EXISTS t_approval_task (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    workflow_id BIGINT NOT NULL COMMENT '工作流ID',
    approver_id BIGINT NOT NULL COMMENT '审批人ID',
    level INT NOT NULL COMMENT '审批级别',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态: PENDING/APPROVED/REJECTED',
    comment VARCHAR(500) COMMENT '审批意见',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    INDEX idx_workflow (workflow_id),
    INDEX idx_approver (approver_id),
    INDEX idx_level (level),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审批任务表';