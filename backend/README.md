# Document Management System - Backend

## 项目结构

```
src/
├── auth/                    # 认证模块
│   ├── auth.controller.ts   # 登录/注册接口
│   ├── auth.module.ts
│   ├── auth.service.ts
│   ├── dto/
│   │   └── auth.dto.ts     # DTO定义
│   ├── jwt.strategy.ts     # JWT策略
│   └── jwt-auth.guard.ts   # 守卫
├── documents/              # 文档CRUD模块
│   ├── documents.controller.ts
│   ├── documents.module.ts
│   ├── documents.service.ts
│   └── dto/
│       └── document.dto.ts
├── entities/               # 数据库实体
│   ├── user.entity.ts
│   └── document.entity.ts
├── app.module.ts
└── main.ts
```

## 启动说明

### 1. 启动 PostgreSQL 数据库
确保 PostgreSQL 已启动并创建数据库：
```bash
createdb docman
```
或使用 Docker:
```bash
docker run -d --name docman-postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=docman -p 5432:5432 postgres:15
```

### 2. 启动后端服务
```bash
cd backend
npm run start:dev
```

## API 接口

### 认证接口
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/login` - 用户登录
- `GET /api/auth/profile` - 获取当前用户信息 (需JWT)

### 文档接口
- `POST /api/documents` - 创建文档
- `GET /api/documents` - 获取文档列表
- `GET /api/documents/:id` - 获取文档详情
- `PUT /api/documents/:id` - 更新文档
- `DELETE /api/documents/:id` - 删除文档

## 环境变量 (.env)
```env
DB_HOST=localhost
DB_PORT=5432
DB_USERNAME=postgres
DB_PASSWORD=postgres
DB_DATABASE=docman
JWT_SECRET=your-secret-key
PORT=3000
```

## 技术栈
- NestJS + TypeScript
- TypeORM + PostgreSQL
- Passport JWT
- bcrypt 密码加密