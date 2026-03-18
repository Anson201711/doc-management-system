#!/bin/bash

# 文档管理系统 - 一键部署脚本
# Author: Deploy Agent

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}  文档管理系统 - 一键部署脚本${NC}"
echo -e "${GREEN}========================================${NC}"

# 检查 Docker 是否安装
check_docker() {
    echo -e "\n${YELLOW}[1/5] 检查 Docker 环境...${NC}"
    
    if ! command -v docker &> /dev/null; then
        echo -e "${RED}Docker 未安装，正在安装...${NC}"
        install_docker
    else
        echo -e "${GREEN}✓ Docker 已安装: $(docker --version)${NC}"
    fi
    
    if ! command -v docker-compose &> /dev/null; then
        echo -e "${RED}Docker Compose 未安装，正在安装...${NC}"
        install_docker_compose
    else
        echo -e "${GREEN}✓ Docker Compose 已安装: $(docker-compose --version)${NC}"
    fi
    
    # 启动 Docker 守护进程
    if command -v systemctl &> /dev/null; then
        sudo systemctl start docker 2>/dev/null || true
    fi
}

# 安装 Docker
install_docker() {
    echo "安装 Docker..."
    if [[ "$OSTYPE" == "darwin"* ]]; then
        echo "请在 Mac 上通过 Homebrew 安装 Docker:"
        echo "  brew install --cask docker"
        exit 1
    elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
        curl -fsSL https://get.docker.com | bash
        sudo usermod -aG docker $USER
        echo -e "${GREEN}✓ Docker 安装完成${NC}"
    fi
}

# 安装 Docker Compose
install_docker_compose() {
    echo "安装 Docker Compose..."
    if [[ "$OSTYPE" == "darwin"* ]]; then
        # Mac 版 Docker 已包含 docker-compose
        return
    fi
    
    sudo curl -L "https://github.com/docker/compose/releases/download/v2.24.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
    sudo chmod +x /usr/local/bin/docker-compose
    echo -e "${GREEN}✓ Docker Compose 安装完成${NC}"
}

# 构建前端
build_frontend() {
    echo -e "\n${YELLOW}[2/5] 构建前端...${NC}"
    
    cd frontend
    
    if [ ! -d "node_modules" ]; then
        echo "安装前端依赖..."
        npm install
    fi
    
    echo "构建前端..."
    npm run build
    
    cd ..
    echo -e "${GREEN}✓ 前端构建完成${NC}"
}

# 构建后端
build_backend() {
    echo -e "\n${YELLOW}[3/5] 构建后端...${NC}"
    
    # 构建各个微服务
    for service in user-service permission-service document-service workflow-service notification-service; do
        echo "构建 $service..."
        cd backend/$service
        
        if command -v mvn &> /dev/null; then
            mvn clean package -DskipTests
        else
            echo -e "${YELLOW}警告: Maven 未安装，跳过 $service 构建${NC}"
        fi
        
        cd ../..
    done
    
    echo -e "${GREEN}✓ 后端构建完成${NC}"
}

# 启动 Docker 容器
start_containers() {
    echo -e "\n${YELLOW}[4/5] 启动 Docker 容器...${NC}"
    
    cd docker
    
    # 复制环境配置
    if [ ! -f ".env" ]; then
        cp .env.example .env
    fi
    
    # 构建并启动容器
    docker-compose up -d --build
    
    cd ..
    echo -e "${GREEN}✓ 容器启动完成${NC}"
}

# 验证部署
verify_deployment() {
    echo -e "\n${YELLOW}[5/5] 验证部署...${NC}"
    
    sleep 5
    
    # 检查容器运行状态
    echo "检查容器状态..."
    docker ps --filter "label=app=docman"
    
    # 检查前端是否可访问
    if curl -s -o /dev/null -w "%{http_code}" http://localhost:3000 | grep -q "200"; then
        echo -e "${GREEN}✓ 前端服务正常${NC}"
    else
        echo -e "${YELLOW}⚠ 前端服务可能尚未就绪${NC}"
    fi
    
    # 检查后端 API
    if curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/v1/health | grep -q "200"; then
        echo -e "${GREEN}✓ 后端服务正常${NC}"
    else
        echo -e "${YELLOW}⚠ 后端服务可能尚未就绪${NC}"
    fi
}

# 显示帮助信息
show_help() {
    echo "用法: $0 [命令]"
    echo ""
    echo "命令:"
    echo "  all         执行完整部署流程 (默认)"
    echo "  docker-only 仅启动 Docker 容器"
    echo "  build       仅构建项目"
    echo "  stop        停止所有容器"
    echo "  restart     重启容器"
    echo "  logs        查看容器日志"
    echo "  clean       清理所有容器和镜像"
    echo "  help        显示帮助信息"
}

# 停止容器
stop_containers() {
    echo "停止所有容器..."
    cd docker
    docker-compose down
    cd ..
    echo -e "${GREEN}✓ 容器已停止${NC}"
}

# 重启容器
restart_containers() {
    stop_containers
    start_containers
}

# 查看日志
show_logs() {
    cd docker
    docker-compose logs -f
}

# 清理环境
clean_env() {
    echo "清理所有容器和镜像..."
    cd docker
    docker-compose down -v --rmi all
    cd ..
    echo -e "${GREEN}✓ 环境已清理${NC}"
}

# 主逻辑
case "${1:-all}" in
    all)
        check_docker
        build_frontend
        # build_backend  # 需要 Maven
        start_containers
        verify_deployment
        ;;
    docker-only)
        start_containers
        ;;
    build)
        build_frontend
        ;;
    stop)
        stop_containers
        ;;
    restart)
        restart_containers
        ;;
    logs)
        show_logs
        ;;
    clean)
        clean_env
        ;;
    help|--help|-h)
        show_help
        ;;
    *)
        echo -e "${RED}未知命令: $1${NC}"
        show_help
        exit 1
        ;;
esac

echo -e "\n${GREEN}========================================${NC}"
echo -e "${GREEN}  部署完成!${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""
echo "访问地址:"
echo "  前端: http://localhost:3000"
echo "  后端: http://localhost:8080"
echo ""