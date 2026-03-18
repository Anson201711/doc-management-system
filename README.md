# 文档管理系统 (Document Management System)

> 企业级文档管理平台，支持文档集中管理、版本控制、权限管理和在线协作。

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Node.js](https://img.shields.io/badge/Node.js-20+-green.svg)](https://nodejs.org)
[![Java](https-orange.svg)](https://www.java.com)
[![React](https://img.shields.io/badge/React-18-blue.svg)](https://react.dev)

## ✨ 功能特性

- 📄 **文档管理** - 支持上传、下载、分类、标签、全文搜索
- 🔄 **版本控制** - 自动记录文档修改历史，支持版本回滚
- 🔐 **权限管理** - 细粒度的文档访问权限控制（7种角色）
- 💬 **在线协作** - 支持多人同时编辑、评论、批注
- ✅ **审批工作流** - 支持文档审批流程
- 🌍 **国际化** - 支持中英文双语

## 🏗️ 技术架构

### 前端技术栈
- React 18 + TypeScript
- Ant Design 5.0
- Vite (构建工具)
- React Router (路由)
- Axios (HTTP 客户端)

### 后端技术栈
- NestJS (Node.js 微服务)
- Spring Cloud (Java 微服务)
- MySQL 8.0 (数据库)
- Redis 7 (缓存)
- Elasticsearch 8 (全文搜索)
- MinIO (对象存储)
- Nacos (服务注册/配置)
- WebSocket (实时协作)

### 架构图

```
┌─────────────────────────────────────────────────────────────┐
│                        Frontend                              │
│                   React + Ant Design                        │
└─────────────────────┬───────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────────┐
│                     API Gateway (NestJS)                    │
│                   (api-gateway service)                     │
└───────┬─────────────┬─────────────┬─────────────┬─────────┘
        │             │             │             │
        ▼             ▼             ▼             ▼
┌───────────┐ ┌───────────┐ ┌───────────┐ ┌───────────┐
│ user      │ │ document  │ │ collab    │ │ workflow  │
│ service   │ │ service   │ │ service   │ │ service   │
└───────────┘ └───────────┘ └───────────┘ └───────────┘
        │             │             │             │
        └─────────────┴─────────────┴─────────────┘
                      │
        ┌─────────────┴─────────────┐
        ▼             ▼             ▼
    ┌──────┐    ┌──────────┐   ┌─────────┐
    │ MySQL│    │ Redis    │   │  MinIO  │
    └──────┘    └──────────┘   └─────────┘
                            │
                      ┌─────┴─────┐
                      │Elasticsearch│
                      └───────────┘
```

## 📁 项目结构

```
├── frontend/                 # 前端项目
│   ├── src/
│   │   ├── components/     # 公共组件
│   │   ├── pages/          # 页面组件
│   │   ├── services/       # API 服务
│   │   ├── routes/         # 路由配置
│   │   └── utils/          # 工具函数
│   └── package.json
│
├── backend/                  # 后端项目 (NestJS Monorepo)
│   ├── src/
│   │   ├── api-gateway/    # API 网关服务
│   │   ├── user-service/   # 用户服务
│   │   ├── document-service/ # 文档服务
│   │   ├── collab-service/ # 协作服务 (评论/批注)
│   │   ├── permission-service/ # 权限服务
│   │   ├── workflow-service/ # 工作流服务
│   │   ├── search-service/ # 搜索服务
│   │   ├── storage-service/ # 存储服务
│   │   └── notification-service/ # 通知服务
│   └── package.json
│
├── docker/                   # Docker 配置
│   └── docker-compose.yml
│
├── docs/                     # 项目文档
│   ├── 需求规格说明书.md
│   ├── 业务架构设计.md
│   ├── 测试计划.md
│   └── 部署文档.md
│
└── README.md
```

## 🚀 快速开始

### 前置要求

- Node.js 20+
- Java 17+
- Docker & Docker Compose

### 本地开发

1. **克隆项目**
```bash
git clone https://github.com/Anson201711/doc-management-system.git
cd doc-management-system
```

2. **启动后端服务**

```bash
cd backend
npm install

# 启动所有微服务 (开发模式)
npm run start:dev

# 或使用 Docker 启动中间件
cd ../docker
docker-compose up -d
```

3. **启动前端**

```bash
cd frontend
npm install
npm run dev
```

4. **访问系统**

- 前端: http://localhost:5173
- API Gateway: http://localhost:3000

### Docker 部署

```bash
# 一键启动所有服务
cd docker
docker-compose up -d
```

## 📋 API 文档

| 模块 | 路径 | 说明 |
|------|------|------|
| 用户 | `/api/v1/users` | 用户注册、登录、个人信息 |
| 文档 | `/api/v1/documents` | 文档 CRUD、版本管理 |
| 协作 | `/api/v1/collab` | 评论、批注、实时协作 |
| 权限 | `/api/v1/permissions` | 权限分配、分享 |
| 工作流 | `/api/v1/workflows` | 审批流程 |
| 搜索 | `/api/v1/search` | 全文搜索 |

详细 API 文档请查看 [API 文档](./docs/API.md)

## 🧪 测试

```bash
# 前端测试
cd frontend
npm test

# 后端测试
cd backend
npm test
```

## 📈 开发进度

| 模块 | 状态 | 进度 |
|------|------|------|
| 项目脚手架 | ✅ 完成 | 100% |
| 用户模块 | 🔄 开发中 | 70% |
| 文档模块 | 🔄 开发中 | 60% |
| 协作模块 | 🔄 开发中 | 50% |
| 权限模块 | 🔄 开发中 | 60% |
| 工作流模块 | ⏳ 待开发 | 0% |
| 搜索模块 | ⏳ 待开发 | 0% |
| 部署 | ⏳ 待部署 | 0% |

## 🤝 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/xxx`)
3. 提交更改 (`git commit -m 'Add xxx'`)
4. 推送分支 (`git push origin feature/xxx`)
5. 创建 Pull Request

## 📄 许可证

MIT License - 查看 [LICENSE](LICENSE) 了解详情

## 👥 团队

- **项目经理**: 邵万松
- **开发团队**: OpenClaw AI Agents

---

Made with ❤️ by OpenClaw