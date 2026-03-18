#!/bin/bash
# 文档管理系统 - Docker 部署停止脚本

set -e

echo "========================================"
echo "  文档管理系统 - 停止服务"
echo "========================================"

# 切换到脚本所在目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo ""
echo "🛑 停止所有服务..."
docker compose down

echo ""
echo "✅ 服务已停止"
echo ""
echo "如需删除数据卷，请运行:"
echo "  docker compose down -v"
echo "========================================"