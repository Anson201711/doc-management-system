#!/bin/bash
# 文档管理系统 - 一键部署脚本

set -e

echo "=========================================="
echo "  文档管理系统 - Docker 部署"
echo "=========================================="

# 颜色定义
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

# 检查 Docker
if ! command -v docker &> /dev/null; then
    echo -e "${RED}错误: Docker 未安装${NC}"
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    echo -e "${RED}错误: docker-compose 未安装${NC}"
    exit 1
fi

echo -e "${GREEN}1. 检查环境...${NC}"
docker --version
docker-compose --version

# 切换到脚本目录
cd "$(dirname "$0")/.."

echo -e "${GREEN}2. 部署中间件 (MySQL/Redis/ES/Nacos/MinIO)...${NC}"
docker-compose -f docker-compose.infra.yml up -d

echo "等待中间件启动..."
sleep 30

echo -e "${GREEN}3. 检查中间件状态...${NC}"
docker ps --filter "name=docman-"

echo -e "${GREEN}4. 部署微服务...${NC}"
docker-compose up -d

echo -e "${GREEN}5. 检查服务状态...${NC}"
docker ps --filter "name=docman-"

echo ""
echo -e "${GREEN}=========================================="
echo "  部署完成！"
echo "==========================================${NC}"
echo ""
echo "服务地址:"
echo "  - 前端:      http://localhost"
echo "  - API网关:   http://localhost:8080"
echo "  - Nacos:     http://localhost:8848 (nacos/nacos)"
echo "  - MinIO:     http://localhost:9001 (minioadmin/minioadmin)"
echo "  - MySQL:     localhost:3306 (docman/docman123)"
echo "  - Redis:     localhost:6379"
echo "  - ES:        localhost:9200"
echo ""