-- 文档管理系统 - document-service 数据库脚本

-- 创建数据库
CREATE DATABASE IF NOT EXISTS docman DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE docman;

-- 文档表
CREATE TABLE IF NOT EXISTS documents (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(500) NOT NULL COMMENT '文档标题',
    content LONGTEXT COMMENT '文档内容',
    folder_id BIGINT COMMENT '文件夹ID',
    creator_id BIGINT NOT NULL COMMENT '创建者ID',
    status VARCHAR(20) DEFAULT 'draft' COMMENT '状态: draft, published, archived',
    document_type VARCHAR(50) COMMENT '文档类型',
    tags JSON DEFAULT JSON_ARRAY() COMMENT '标签',
    current_version INT DEFAULT 1 COMMENT '当前版本号',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_folder_id (folder_id),
    INDEX idx_creator_id (creator_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文档表';

-- 文档版本表
CREATE TABLE IF NOT EXISTS doc_versions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    document_id BIGINT NOT NULL COMMENT '文档ID',
    version INT NOT NULL COMMENT '版本号',
    content LONGTEXT COMMENT '版本内容',
    change_summary VARCHAR(500) COMMENT '变更摘要',
    creator_id BIGINT NOT NULL COMMENT '创建者ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (document_id) REFERENCES documents(id) ON DELETE CASCADE,
    FOREIGN KEY (creator_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_doc_version (document_id, version)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文档版本表';

-- 文件夹表
CREATE TABLE IF NOT EXISTS folders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL COMMENT '文件夹名称',
    parent_id BIGINT COMMENT '父文件夹ID',
    owner_id BIGINT NOT NULL COMMENT '所有者ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (parent_id) REFERENCES folders(id) ON DELETE CASCADE,
    FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_parent_id (parent_id),
    INDEX idx_owner_id (owner_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件夹表';

-- 附件表
CREATE TABLE IF NOT EXISTS attachments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    document_id BIGINT NOT NULL COMMENT '文档ID',
    file_name VARCHAR(255) NOT NULL COMMENT '文件名',
    file_size BIGINT COMMENT '文件大小',
    file_type VARCHAR(100) COMMENT '文件类型',
    storage_key VARCHAR(500) NOT NULL COMMENT '存储Key',
    uploader_id BIGINT NOT NULL COMMENT '上传者ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (document_id) REFERENCES documents(id) ON DELETE CASCADE,
    FOREIGN KEY (uploader_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_document_id (document_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='附件表';