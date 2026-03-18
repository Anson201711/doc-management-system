# 部署步骤说明

## 环境要求

| 组件 | 版本要求 | 端口 |
|------|---------|------|
| OS | Ubuntu 20.04+ / CentOS 8+ | - |
| Docker | 24.0+ | - |
| Docker Compose | v2.24+ | - |
| Java | 17 (构建用) | - |
| Maven | 3.8+ (构建用) | - |
| Node.js | 18+ (构建用) | - |

## 部署步骤

### 步骤 1: 环境准备

```bash
# 1.1 安装 Docker (如未安装)
curl -fsSL https://get.docker.com | sh
sudo systemctl enable docker
sudo systemctl start docker
sudo usermod -aG docker $USER

# 1.2 安装 Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/download/v2.24.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# 1.3 验证安装
docker --version
docker-compose --version
```

### 步骤 2: DEPLOY-01 部署中间件

```bash
# 进入 Docker 配置目录
cd /path/to/workspace-pm/docker

# 启动基础设施 (MySQL, Redis, ES, Nacos, MinIO)
docker-compose -f docker-compose.infra.yml up -d

# 等待服务启动 (约 60 秒)
sleep 60

# 验证中间件状态
docker ps
```

**预期容器:**
- docman-mysql (3306)
- docman-redis (6379)
- docman-elasticsearch (9200)
- docman-nacos (8848)
- docman-minio (9000/9001)

### 步骤 3: DEPLOY-02 构建部署微服务

```bash
# 进入后端目录
cd /path/to/workspace-pm/backend

# 安装 Java 17 和 Maven (如未安装)
# 以 Ubuntu 为例:
# sudo apt install openjdk-17-jdk maven

# 构建各微服务
for service in user-service document-service permission-service workflow-service notification-service search-service storage-service api-gateway collab-service; do
    cd $service
    mvn clean package -DskipTests
    cd ..
done

# 返回 Docker 目录构建镜像
cd ../docker
docker-compose build

# 启动微服务
docker-compose up -d
```

**注意:** 当前项目后端代码使用 NestJS + Node.js 运行，需使用 Dockerfile.node 构建。

### 步骤 4: DEPLOY-03 前端构建部署

```bash
# 进入前端目录
cd /path/to/workspace-pm/frontend

# 安装依赖
npm install

# 构建
npm run build

# 产物位于 dist/ 目录
# Docker-compose 会自动挂载到 Nginx
```

### 步骤 5: DEPLOY-04 Nacos 服务注册

```bash
# 访问 Nacos 控制台
# http://your-server:8848/nacos
# 默认账号: nacos / nacos

# 1. 创建命名空间: docman

# 2. 注册服务 (通过配置文件或 API)
# 服务会自动注册到 Nacos (Spring Cloud 集成)
```

### 步骤 6: DEPLOY-05 Nginx 配置

```bash
# Nginx 配置已在 docker/nginx.conf 中定义
# 主要配置:
# - 静态文件: / -> /usr/share/nginx/html
# - API 代理: /api -> user-service:8081
# - WebSocket: /ws -> collab-service

# 如需自定义, 编辑 docker/nginx.conf
vim docker/nginx.conf

# 重载 Nginx
docker exec docman-nginx nginx -s reload
```

### 步骤 7: DEPLOY-06 部署验证

```bash
# 检查所有容器状态
docker ps

# 验证各服务
curl http://localhost:8080/api/v1/health     # API网关
curl http://localhost:8848/nacos/v1/console/health/readiness  # Nacos
curl http://localhost:9200                   # Elasticsearch
curl http://localhost:9000/minio/health/minio  # MinIO
```

## 服务访问地址

| 服务 | 地址 |
|------|------|
| 前端 | http://localhost:8080 |
| API网关 | http://localhost:8080/api |
| Nacos控制台 | http://localhost:8848/nacos |
| MinIO控制台 | http://localhost:9001 |
| Elasticsearch | http://localhost:9200 |

## 常见问题

### 1. 端口冲突
```bash
# 检查端口占用
lsof -i :3306
lsof -i :6379
# 修改 docker-compose.yml 中的端口映射
```

### 2. 内存不足
```bash
# ES 需要至少 4GB 内存
# 修改 docker-compose.infra.yml 中 ES_JAVA_OPTS
```

### 3. 构建失败
```bash
# 检查 Java/Maven/Node 版本
java -version
mvn -version
node -v
npm -v
```

## 回滚操作

```bash
# 停止所有服务
docker-compose down

# 删除数据卷 (慎用)
docker-compose down -v

# 删除镜像
docker-compose down --rmi all
```