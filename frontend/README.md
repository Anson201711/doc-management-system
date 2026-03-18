# 文档管理系统前端

基于 React 18 + TypeScript + Ant Design 5.0 的文档管理系统前端项目。

## 技术栈

- React 18
- TypeScript
- Ant Design 5.0
- React Router 6
- Axios
- Vite

## 项目结构

```
frontend/
├── src/
│   ├── assets/          # 静态资源
│   ├── components/      # 公共组件
│   │   ├── common/      # 通用组件
│   │   ├── form/        # 表单组件
│   │   ├── layout/      # 布局组件
│   │   └── table/       # 表格组件
│   ├── context/         # React Context
│   ├── hooks/           # 自定义 Hooks
│   ├── pages/           # 页面组件
│   ├── router/          # 路由配置
│   ├── services/       # API 服务
│   ├── types/           # TypeScript 类型定义
│   ├── utils/           # 工具函数
│   ├── App.tsx
│   ├── main.tsx
│   └── index.css
├── public/
├── index.html
├── package.json
├── tsconfig.json
├── vite.config.ts
└── .env
```

## 快速开始

### 安装依赖

```bash
cd frontend
npm install
```

### 启动开发服务器

```bash
npm run dev
```

项目将在 http://localhost:3000 启动。

### 构建生产版本

```bash
npm run build
```

## 功能特性

- 用户认证 (登录/登出)
- 文档管理 (CRUD)
- 文件夹管理 (树形结构)
- 搜索功能
- 权限管理
- 审批流程
- 响应式布局

## API 文档

参考后端架构文档中的 API 接口规范。