-- 插入测试部门
INSERT INTO departments (id, name, parent_id, code, description, sort_order, status) VALUES
(1, '技术部', NULL, 'TECH', '技术研发部门', 1, 'active'),
(2, '产品部', NULL, 'PRODUCT', '产品设计部门', 2, 'active'),
(3, '销售部', NULL, 'SALES', '销售部门', 3, 'active'),
(4, '研发组', 1, 'DEV', '开发团队', 1, 'active'),
(5, '测试组', 1, 'QA', '测试团队', 2, 'active');

-- 插入测试用户 (密码: password123, BCrypt加密)
INSERT INTO users (id, username, email, password_hash, full_name, avatar_url, phone, department_id, status) VALUES
(1, 'admin', 'admin@docman.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '系统管理员', NULL, '13800138000', 1, 'active'),
(2, 'testuser', 'test@docman.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '测试用户', NULL, '13800138001', 4, 'active');