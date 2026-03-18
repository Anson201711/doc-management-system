-- 权限服务数据库表结构
-- 创建数据库
CREATE DATABASE IF NOT EXISTS docman CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE docman;

-- 角色表
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE COMMENT '角色名称',
    description VARCHAR(255) COMMENT '角色描述',
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态: active, inactive',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_name (name),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 权限表
CREATE TABLE IF NOT EXISTS permissions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '权限名称',
    resource VARCHAR(100) NOT NULL COMMENT '资源标识',
    action VARCHAR(50) NOT NULL COMMENT '操作标识',
    description VARCHAR(255) COMMENT '权限描述',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_resource_action (resource, action)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS role_permissions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE,
    UNIQUE KEY uk_role_perm (role_id, permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS user_roles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_role (user_id, role_id),
    INDEX idx_user_id (user_id),
    INDEX idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 文档权限表 - 细粒度权限控制
CREATE TABLE IF NOT EXISTS doc_permissions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    document_id BIGINT NOT NULL COMMENT '文档ID',
    user_id BIGINT COMMENT '用户ID (与role_id二选一)',
    role_id BIGINT COMMENT '角色ID (与user_id二选一)',
    permission_type VARCHAR(20) NOT NULL COMMENT '权限类型: read, write, admin',
    expiry_date DATETIME COMMENT '过期时间',
    created_by BIGINT NOT NULL COMMENT '创建者ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_doc_user (document_id, user_id),
    UNIQUE KEY uk_doc_role (document_id, role_id),
    INDEX idx_document_id (document_id),
    INDEX idx_user_id (user_id),
    INDEX idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文档权限表';

-- 初始化数据
-- 插入默认角色
INSERT INTO roles (name, description, status) VALUES 
('超级管理员', '拥有所有权限', 'active'),
('文档管理员', '管理所有文档', 'active'),
('普通用户', '基础用户角色', 'active'),
('访客', '只读访问', 'active')
ON DUPLICATE KEY UPDATE description = VALUES(description);

-- 插入默认权限
INSERT INTO permissions (name, resource, action, description) VALUES
('查看文档', 'document', 'read', '查看文档内容'),
('编辑文档', 'document', 'write', '编辑文档内容'),
('删除文档', 'document', 'delete', '删除文档'),
('管理文档权限', 'document', 'admin', '管理文档权限'),
('创建文档', 'document', 'create', '创建新文档'),
('查看用户', 'user', 'read', '查看用户信息'),
('管理用户', 'user', 'admin', '管理用户'),
('查看角色', 'role', 'read', '查看角色'),
('管理角色', 'role', 'admin', '管理角色')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- 关联超级管理员角色和所有权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT 1, id FROM permissions
ON DUPLICATE KEY UPDATE role_id = role_id;

-- 关联文档管理员角色权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT 2, id FROM permissions WHERE resource = 'document'
ON DUPLICATE KEY UPDATE role_id = role_id;

-- 关联普通用户角色权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT 3, id FROM permissions WHERE resource = 'document' AND action IN ('read', 'write', 'create')
ON DUPLICATE KEY UPDATE role_id = role_id;

-- 关联访客角色权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT 4, id FROM permissions WHERE resource = 'document' AND action = 'read'
ON DUPLICATE KEY UPDATE role_id = role_id;