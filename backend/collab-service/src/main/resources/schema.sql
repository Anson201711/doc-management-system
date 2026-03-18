-- Comments table
CREATE TABLE IF NOT EXISTS comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    document_id BIGINT NOT NULL COMMENT 'Document ID',
    user_id BIGINT NOT NULL COMMENT 'User ID',
    parent_id BIGINT DEFAULT NULL COMMENT 'Parent comment ID for replies',
    content TEXT NOT NULL COMMENT 'Comment content',
    status VARCHAR(20) DEFAULT 'active' COMMENT 'active, deleted',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
    INDEX idx_document_id (document_id),
    INDEX idx_user_id (user_id),
    INDEX idx_parent_id (parent_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Comments table';

-- Annotations table
CREATE TABLE IF NOT EXISTS annotations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    document_id BIGINT NOT NULL COMMENT 'Document ID',
    user_id BIGINT NOT NULL COMMENT 'User ID',
    version_id BIGINT DEFAULT NULL COMMENT 'Document version ID',
    page_number INT DEFAULT 1 COMMENT 'Page number',
    position JSON COMMENT 'Position data {x, y, width, height}',
    content TEXT NOT NULL COMMENT 'Annotation content',
    color VARCHAR(20) DEFAULT '#FFEB3B' COMMENT 'Annotation color',
    creator_id BIGINT NOT NULL COMMENT 'Creator user ID',
    status VARCHAR(20) DEFAULT 'active' COMMENT 'active, deleted',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
    INDEX idx_document_id (document_id),
    INDEX idx_user_id (user_id),
    INDEX idx_version_id (version_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Annotations table';