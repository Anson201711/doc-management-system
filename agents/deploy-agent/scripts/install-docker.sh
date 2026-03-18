#!/bin/bash

# Docker 安装脚本 - 文档管理系统
# 支持 macOS 和 Linux

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}  Docker 安装脚本${NC}"
echo -e "${BLUE}========================================${NC}"

# 检测操作系统
detect_os() {
    if [[ "$OSTYPE" == "darwin"* ]]; then
        echo "macOS"
    elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
        echo "Linux"
    else
        echo "Unknown"
    fi
}

# 安装 Docker (macOS)
install_docker_mac() {
    echo -e "\n${YELLOW}检测到 macOS 系统${NC}"
    
    if command -v docker &> /dev/null; then
        echo -e "${GREEN}✓ Docker 已安装: $(docker --version)${NC}"
        return 0
    fi
    
    echo "正在安装 Docker Desktop for Mac..."
    echo ""
    echo "请选择安装方式:"
    echo "  1. Homebrew: brew install --cask docker"
    echo "  2. 手动下载: https://www.docker.com/products/docker-desktop"
    echo ""
    echo "安装完成后，请运行: open -a Docker"
    echo ""
    
    # 尝试使用 Homebrew 安装
    if command -v brew &> /dev/null; then
        read -p "是否使用 Homebrew 自动安装? (y/n): " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            echo "正在安装 Docker Desktop..."
            brew install --cask docker
            echo -e "${GREEN}✓ Docker 安装完成${NC}"
            echo "请启动 Docker Desktop 并等待其运行"
            open -a Docker
        fi
    fi
}

# 安装 Docker (Linux)
install_docker_linux() {
    echo -e "\n${YELLOW}检测到 Linux 系统${NC}"
    
    if command -v docker &> /dev/null; then
        echo -e "${GREEN}✓ Docker 已安装: $(docker --version)${NC}"
        return 0
    fi
    
    echo "正在安装 Docker..."
    
    # 安装依赖
    sudo apt-get update
    sudo apt-get install -y \
        ca-certificates \
        curl \
        gnupg \
        lsb-release
    
    # 添加 Docker GPG 密钥
    sudo mkdir -p /etc/apt/keyrings
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
    
    # 添加 Docker 仓库
    echo \
        "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
        $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
    
    # 安装 Docker
    sudo apt-get update
    sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
    
    # 启动 Docker
    sudo systemctl start docker
    sudo systemctl enable docker
    
    # 添加用户到 docker 组
    sudo usermod -aG docker $USER
    
    echo -e "${GREEN}✓ Docker 安装完成${NC}"
    echo "请重新登录以使组权限生效，或运行: newgrp docker"
}

# 安装 Docker Compose (独立版本)
install_docker_compose() {
    echo -e "\n${YELLOW}检查 Docker Compose...${NC}"
    
    if command -v docker-compose &> /dev/null; then
        echo -e "${GREEN}✓ Docker Compose 已安装: $(docker-compose --version)${NC}"
        return 0
    fi
    
    if command -v docker &> /dev/null && docker compose version &> /dev/null; then
        echo -e "${GREEN}✓ Docker Compose (v2) 已集成在 Docker 中${NC}"
        return 0
    fi
    
    echo "安装 Docker Compose..."
    
    if [[ "$OSTYPE" == "darwin"* ]]; then
        echo "Docker Desktop for Mac 已包含 Docker Compose"
    else
        sudo curl -L "https://github.com/docker/compose/releases/download/v2.24.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
        sudo chmod +x /usr/local/bin/docker-compose
        echo -e "${GREEN}✓ Docker Compose 安装完成${NC}"
    fi
}

# 验证 Docker 安装
verify_docker() {
    echo -e "\n${YELLOW}验证 Docker 安装...${NC}"
    
    # 启动 Docker (Linux)
    if [[ "$OSTYPE" == "linux-gnu"* ]]; then
        sudo systemctl start docker 2>/dev/null || true
    fi
    
    # 等待 Docker 启动
    echo "等待 Docker 启动..."
    local max_attempts=30
    local attempt=0
    
    while [ $attempt -lt $max_attempts ]; do
        if docker info &> /dev/null; then
            echo -e "${GREEN}✓ Docker 运行正常${NC}"
            docker --version
            return 0
        fi
        sleep 1
        attempt=$((attempt + 1))
    done
    
    echo -e "${RED}✗ Docker 启动失败${NC}"
    echo "请手动启动 Docker 后重试"
    return 1
}

# 主逻辑
main() {
    OS=$(detect_os)
    echo -e "检测到操作系统: ${GREEN}$OS${NC}"
    
    case $OS in
        macOS)
            install_docker_mac
            ;;
        Linux)
            install_docker_linux
            ;;
        *)
            echo -e "${RED}不支持的操作系统${NC}"
            exit 1
            ;;
    esac
    
    install_docker_compose
    verify_docker
    
    echo ""
    echo -e "${GREEN}========================================${NC}"
    echo -e "${GREEN}  Docker 安装完成!${NC}"
    echo -e "${GREEN}========================================${NC}"
    echo ""
    echo "下一步操作:"
    echo "  1. 进入部署目录: cd docker"
    echo "  2. 运行部署脚本: ./deploy.sh all"
    echo ""
}

main "$@"