#!/bin/bash
# 文档管理系统 - 中间件镜像拉取脚本
# 运行方式: chmod +x pull-images.sh && ./pull-images.sh

set -e

echo "=========================================="
echo "文档管理系统 - 中间件镜像拉取脚本"
echo "=========================================="

# 中间件镜像列表
IMAGES=(
    "mysql:8.0"
    "redis:7-alpine"
    "elasticsearch:8.11.0"
    "minio/minio:latest"
    "nacos/nacos-server:v2.2.3"
    "nginx:alpine"
)

echo ""
echo "开始拉取中间件镜像..."
echo ""

for image in "${IMAGES[@]}"; do
    echo "正在拉取: $image"
    docker pull "$image"
    echo "✓ $image 拉取完成"
    echo ""
done

echo "=========================================="
echo "所有中间件镜像拉取完成!"
echo "=========================================="
echo ""
echo "启动中间件:"
echo "  docker-compose -f docker-compose.infra.yml up -d"
echo ""
echo "检查容器状态:"
echo "  docker-compose -f docker-compose.infra.yml ps"
echo ""
echo "查看日志:"
echo "  docker-compose -f docker-compose.infra.yml logs -f"
echo ""
echo "服务端口:"
echo "  MySQL:       3306"
echo "  Redis:       6379"
echo "  Elasticsearch: 9200"
echo "  MinIO:       9000 (控制台: 9001)"
echo "  Nacos:       8848"
echo "  Nginx:       8080"
echo "=========================================="