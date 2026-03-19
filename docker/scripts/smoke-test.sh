#!/bin/bash
# 冒烟测试脚本 - 验证系统核心功能

set -e

# 配置
API_BASE="http://localhost:8080"
REDIS_HOST="localhost"
MYSQL_HOST="localhost"

# 颜色定义
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

PASS=0
FAIL=0

pass() {
    echo -e "${GREEN}✓${NC} $1"
    ((PASS++))
}

fail() {
    echo -e "${RED}✗${NC} $1"
    ((FAIL++))
}

info() {
    echo -e "${YELLOW}ℹ${NC} $1"
}

echo "=========================================="
echo "  文档管理系统 - 冒烟测试"
echo "=========================================="
echo ""

# 1. 检查中间件
echo "【1】中间件健康检查"
echo "------------------------"

# MySQL
if docker exec docman-mysql mysqladmin ping -uroot -proot123 &>/dev/null; then
    pass "MySQL 运行正常"
else
    fail "MySQL 未运行"
fi

# Redis
if docker exec docman-redis redis-cli ping &>/dev/null; then
    pass "Redis 运行正常"
else
    fail "Redis 未运行"
fi

# Elasticsearch
if curl -s http://localhost:9200 | grep -q "cluster_name"; then
    pass "Elasticsearch 运行正常"
else
    fail "Elasticsearch 未运行"
fi

# MinIO
if curl -s http://localhost:9000/minio/health/live | grep -q "ok"; then
    pass "MinIO 运行正常"
else
    fail "MinIO 未运行"
fi

# Nacos
if curl -s http://localhost:8848/nacos/v1/console/health/readiness | grep -q "ok"; then
    pass "Nacos 运行正常"
else
    fail "Nacos 未运行"
fi

echo ""

# 2. API 服务健康检查
echo "【2】微服务健康检查"
echo "------------------------"

services=(
    "8080:API Gateway"
    "8081:用户服务"
    "8082:权限服务"
    "8083:文档服务"
    "8084:工作流服务"
    "8085:通知服务"
    "8086:协作服务"
    "8087:搜索服务"
    "8088:存储服务"
)

for item in "${services[@]}"; do
    port="${item%%:*}"
    name="${item##*:}"
    if curl -s "http://localhost:$port/actuator/health" | grep -q "UP" 2>/dev/null; then
        pass "$name ($port) UP"
    else
        fail "$name ($port) DOWN"
    fi
done

echo ""

# 3. 核心功能测试
echo "【3】核心功能测试"
echo "------------------------"

# 用户登录
info "测试用户登录..."
LOGIN_RESP=$(curl -s -X POST "http://localhost:8080/api/user/login" \
    -H "Content-Type: application/json" \
    -d '{"username":"admin","password":"password"}')

if echo "$LOGIN_RESP" | grep -q "token"; then
    pass "用户登录功能正常"
    TOKEN=$(echo "$LOGIN_RESP" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
else
    fail "用户登录失败"
    TOKEN=""
fi

if [ -n "$TOKEN" ]; then
    # 文档列表
    info "测试获取文档列表..."
    DOCS_RESP=$(curl -s "http://localhost:8080/api/document/list" \
        -H "Authorization: Bearer $TOKEN")
    if echo "$DOCS_RESP" | grep -q "data"; then
        pass "获取文档列表正常"
    else
        fail "获取文档列表失败"
    fi

    # 文档上传
    info "测试文档上传..."
    UPLOAD_RESP=$(curl -s -X POST "http://localhost:8080/api/document/upload" \
        -H "Authorization: Bearer $TOKEN" \
        -F "file=@/etc/hosts")
    if echo "$UPLOAD_RESP" | grep -qE "(success|id)"; then
        pass "文档上传功能正常"
    else
        fail "文档上传失败"
    fi
fi

echo ""

# 4. WebSocket 连接测试
echo "【4】WebSocket 测试"
echo "------------------------

info "测试 WebSocket 连接..."
# 简单测试 WebSocket 端口是否开放
if curl -s "http://localhost:8086/actuator/health" | grep -q "UP"; then
    pass "协作服务 WebSocket 就绪"
else
    fail "协作服务未就绪"
fi

echo ""

# 5. 搜索功能测试
echo "【5】搜索功能测试"
echo "------------------------"

info "测试全文搜索..."
SEARCH_RESP=$(curl -s "http://localhost:8087/api/search" \
    -H "Content-Type: application/json" \
    -d '{"keyword":"架构","page":1,"size":10}')
if echo "$SEARCH_RESP" | grep -qE "(data|results)"; then
    pass "搜索功能正常"
else
    fail "搜索功能异常"
fi

echo ""
echo "=========================================="
echo "  测试结果汇总"
echo "=========================================="
echo -e "${GREEN}通过: $PASS${NC}"
echo -e "${RED}失败: $FAIL${NC}"
echo ""

if [ $FAIL -eq 0 ]; then
    echo -e "${GREEN}✓ 所有测试通过！系统部署成功！${NC}"
    exit 0
else
    echo -e "${RED}✗ 存在失败项，请检查日志${NC}"
    exit 1
fi