#!/bin/bash
# 测试数据准备脚本

set -e

echo "=========================================="
echo "  准备测试数据"
echo "=========================================="

# 配置
MYSQL_HOST="localhost"
MYSQL_PORT="3306"
MYSQL_USER="docman"
MYSQL_PASS="docman123"
MYSQL_DB="docman"

# 等待 MySQL 就绪
echo "等待 MySQL 就绪..."
until mysql -h"$MYSQL_HOST" -P"$MYSQL_PORT" -u"$MYSQL_USER" -p"$MYSQL_PASS" -e "SELECT 1" &>/dev/null; do
    echo "等待中..."
    sleep 2
done
echo "MySQL 已就绪"

# 创建测试用户
echo "创建测试用户..."
mysql -h"$MYSQL_HOST" -P"$MYSQL_PORT" -u"$MYSQL_USER" -p"$MYSQL_PASS" "$MYSQL_DB" <<EOF
-- 插入测试用户
INSERT INTO sys_user (id, username, password, email, nickname, status, create_time) VALUES
(1, 'admin', '\$2a\$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'admin@docman.com', '管理员', 1, NOW()),
(2, 'testuser', '\$2a\$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test@docman.com', '测试用户', 1, NOW()),
(3, 'viewer', '\$2a\$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'viewer@docman.com', '查看者', 1, NOW())
ON DUPLICATE KEY UPDATE username=username;

-- 插入测试文档分类
INSERT INTO document_folder (id, name, parent_id, owner_id, create_time) VALUES
(1, '技术文档', NULL, 1, NOW()),
(2, '产品文档', NULL, 1, NOW()),
(3, '项目文档', NULL, 1, NOW())
ON DUPLICATE KEY UPDATE name=name;

-- 插入测试文档
INSERT INTO document (id, title, content, folder_id, owner_id, status, create_time, update_time) VALUES
(1, '系统架构设计', '# 文档管理系统架构设计\n\n## 总体架构\n\n采用 Spring Cloud 微服务架构...', 1, 1, 1, NOW(), NOW()),
(2, 'API 接口文档', '# API 接口文档\n\n## 用户服务\n\n### 登录接口\n\nPOST /api/user/login', 1, 1, 1, NOW(), NOW()),
(3, '产品需求文档', '# 产品需求文档\n\n## 需求概述\n\n建设统一的文档管理平台...', 2, 1, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE title=title;

-- 插入测试权限
INSERT INTO document_permission (id, document_id, user_id, permission_type, create_time) VALUES
(1, 1, 2, 'edit', NOW()),
(2, 1, 3, 'view', NOW()),
(3, 2, 2, 'edit', NOW()),
(4, 3, 2, 'view', NOW())
ON DUPLICATE KEY UPDATE document_id=document_id;

-- 插入测试审批流程
INSERT INTO approval_workflow (id, document_id, title, status, applicant_id, create_time) VALUES
(1, 1, '系统架构设计审批', 'pending', 1, NOW())
ON DUPLICATE KEY UPDATE title=title;

-- 插入测试通知
INSERT INTO notification (id, user_id, title, content, type, status, create_time) VALUES
(1, 1, '欢迎使用', '欢迎使用文档管理系统', 'system', 0, NOW()),
(2, 2, '文档分享', '管理员分享了"系统架构设计"给您', 'share', 0, NOW())
ON DUPLICATE KEY UPDATE title=title;

-- 插入测试评论
INSERT INTO document_comment (id, document_id, user_id, content, create_time) VALUES
(1, 1, 2, '这个架构设计很清晰，赞！', NOW()),
(2, 1, 3, '期待实现！', NOW())
ON DUPLICATE KEY UPDATE document_id=document_id;
EOF

echo "测试数据创建完成!"
echo ""
echo "测试账号:"
echo "  - 管理员: admin / password"
echo "  - 测试用户: testuser / password"
echo "  - 查看者: viewer / password"