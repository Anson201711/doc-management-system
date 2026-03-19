#!/bin/bash
# 停止所有服务

set -e

echo "停止文档管理系统..."

cd "$(dirname "$0")/.."

# 停止所有容器
docker-compose down
docker-compose -f docker-compose.infra.yml down

echo "所有服务已停止"