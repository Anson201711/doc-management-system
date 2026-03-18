#!/bin/bash
# 文档管理系统 - Docker 部署启动脚本

set -e

echo "========================================"
echo "  文档管理系统 - Docker 部署"
echo "========================================"

# 检查 Docker 和 Docker Compose
if ! command -v docker &> /dev/null; then
    echo "❌ Docker 未安装，请先安装 Docker"
    exit 1
fi

if ! command -v docker compose &> /dev/null; then
    echo "❌ Docker Compose 未安装"
    exit 1
fi

# 切换到脚本所在目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo ""
echo "📦 构建并启动所有服务..."
echo ""

# 构建并启动所有服务
docker compose up -d --build

echo ""
echo "⏳ 等待服务启动..."
sleep 10

echo ""
echo "📊 检查服务状态..."
docker compose ps

echo ""
echo "✅ 部署完成！"
echo ""
echo "访问地址："
echo "  - 前端: http://localhost"
echo "  - API网关: http://localhost:8080"
echo ""
echo "服务端口："
echo "  - Nginx: 80"
echo "  - API Gateway: 8080"
echo "  - User Service: 8081"
echo "  - Permission Service: 8082"
echo "  - Document Service: 8083"
echo "  - Workflow Service: 8084"
echo "  - Notification Service: 8085"
echo "  - MySQL: 3306"
echo "  - Redis: 6379"
echo "  - Elasticsearch: 9200"
echo "  - MinIO: 9000"
echo ""
echo "查看日志: docker compose logs -f [服务名]"
echo "停止服务: ./stop.sh"
echo "========================================"