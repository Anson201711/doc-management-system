# GitHub CI/CD 配置说明

本文档说明如何通过 Jenkins 从 GitHub 获取代码并执行 CI/CD。

## 前置条件

1. **Jenkins 已部署** (见 DEPLOY-01 已安装)
2. **GitHub 仓库** 已创建
3. **Jenkins 配置**:
   - 安装插件: Git, Pipeline, Docker, Email Extension
   - 配置 GitHub Credentials
   - 配置 Docker Registry Credentials

## 配置步骤

### 1. 在 GitHub 创建 Repository

```bash
# 本地初始化
cd /Users/infodba/.openclaw/workspace/workspace-pm

# 添加所有代码
git init
git add .
git commit -m "Initial commit: Document Management System"

# 创建 GitHub repository 并推送
git remote add origin https://github.com/your-org/doc-management-system.git
git branch -M main
git push -u origin main
```

### 2. Jenkins 配置

#### 2.1 创建 GitHub Credentials
- Jenkins → Credentials → Add Credentials
- Kind: Username with password
- Username: your-github-username
- Password: github-token
- ID: github-credentials

#### 2.2 创建 Pipeline Job
- Jenkins → New Item → Pipeline
- Name: doc-management-system
- Configure:
  - ✓ GitHub project: https://github.com/your-org/doc-management-system/
  - Build Triggers: ✓ GitHub hook trigger for GITScm polling
  - Pipeline: Definition → Pipeline script from SCM
    - SCM: Git
    - Repository URL: https://github.com/your-org/doc-management-system.git
    - Credentials: github-credentials
    - Branch: */main
    - Script Path: jenkins/Jenkinsfile

### 3. 配置环境变量

在 Jenkins Pipeline 中配置以下环境变量:

| 变量名 | 说明 | 示例值 |
|--------|------|--------|
| GIT_REPO | GitHub 仓库地址 | https://github.com/your-org/doc-management-system.git |
| BRANCH | 分支 | main |
| DOCKER_REGISTRY | Docker 镜像仓库 | docker.io |
| DOCKER_USERNAME | 仓库用户名 | your-username |
| DOCKER_PASSWORD | 仓库密码 | your-password |
| DEPLOY | 是否部署 | true |
| DEPLOY_HOST | 部署服务器IP | 192.168.1.100 |
| DEPLOY_USER | SSH 用户 | root |
| PUSH_DOCKER | 是否推送镜像 | true |
| EMAIL_TO | 通知邮箱 | dev@company.com |

### 4. 手动触发构建

```bash
# 通过 Jenkins API 触发
curl -X POST http://jenkins:jenkins-token@jenkins-server:8089/job/doc-management-system/build
```

### 5. Webhook 自动触发 (推荐)

1. 在 GitHub Repository → Settings → Webhooks
2. Add webhook:
   - Payload URL: http://jenkins-server:8089/github-webhook/
   - Content type: application/json
   - Events: ✓ Pushes

## Pipeline 执行流程

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│  Checkout   │────▶│ Build       │────▶│  Build      │
│  GitHub     │     │  Backend    │     │  Frontend   │
└─────────────┘     └─────────────┘     └─────────────┘
                                                │
                                                ▼
                    ┌─────────────┐     ┌─────────────┐
                    │ Health      │◀────│  Docker     │
                    │ Check       │     │  Deploy     │
                    └─────────────┘     └─────────────┘
```

## 文件清单

| 文件 | 说明 |
|------|------|
| jenkins/Jenkinsfile | CI/CD 流水线脚本 |
| frontend/Dockerfile | 前端构建镜像 |
| frontend/nginx.conf | Nginx 配置 |
| docker/docker-compose.prod.yml | 生产环境部署配置 |

## 常用命令

```bash
# 手动构建 (本地)
cd backend && mvn clean package -DskipTests
cd frontend && npm run build

# 本地 Docker 构建
docker-compose -f docker/docker-compose.prod.yml build

# 本地部署
docker-compose -f docker/docker-compose.prod.yml up -d

# 查看日志
docker-compose -f docker/docker-compose.prod.yml logs -f

# 停止
docker-compose -f docker/docker-compose.prod.yml down
```

## 注意事项

1. 确保 Jenkins 服务器已安装 Docker 和 Maven
2. 前端构建需要 Node.js 18+
3. 如果不推送到镜像仓库,设置 `PUSH_DOCKER=false`
4. 生产部署需要配置 SSH 免密登录