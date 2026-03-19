# API 接口文档

本文档详细描述了文档管理系统的所有 REST API 接口。

## 目录

- [用户服务 (User Service)](#用户服务-user-service)
- [权限服务 (Permission Service)](#权限服务-permission-service)
- [文档服务 (Document Service)](#文档服务-document-service)
- [工作流服务 (Workflow Service)](#工作流服务-workflow-service)
- [通知服务 (Notification Service)](#通知服务-notification-service)
- [协作服务 (Collab Service)](#协作服务-collab-service)
- [搜索服务 (Search Service)](#搜索服务-search-service)
- [存储服务 (Storage Service)](#存储服务-storage-service)

---

## 基础信息

| 属性 | 值 |
|------|-----|
| 基础 URL | `http://localhost:8080` |
| 数据格式 | JSON |
| 认证方式 | Bearer Token (JWT) |
| 编码 | UTF-8 |

## 通用响应格式

### 成功响应 (200)

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

### 错误响应 (400)

```json
{
  "code": 400,
  "message": "错误描述"
}
```

### 未授权 (401)

```json
{
  "code": 401,
  "message": "未提供认证 token"
}
```

---

## 用户服务 (User Service)

**端口**: 8081  
**基础路径**: `/api/v1`

### 1. 用户登录

**接口**: `POST /api/v1/auth/login`

**描述**: 用户登录系统，返回 JWT 令牌

**请求参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| username | String | 是 | 用户名 |
| password | String | 是 | 密码 |

**请求体示例**:

```json
{
  "username": "admin",
  "password": "123456"
}
```

**响应示例 (200)**:

```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400,
    "user": {
      "id": 1,
      "username": "admin",
      "email": "admin@example.com",
      "fullName": "管理员"
    }
  }
}
```

**错误响应 (401)**:

```json
{
  "code": 401,
  "message": "用户名或密码错误"
}
```

**CURL 示例**:

```bash
curl -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'
```

---

### 2. 用户注册

**接口**: `POST /api/v1/auth/register`

**描述**: 注册新用户

**请求参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| username | String | 是 | 用户名 (3-20字符) |
| password | String | 是 | 密码 (6-20字符) |
| email | String | 是 | 邮箱 |
| fullName | String | 否 | 真实姓名 |
| phone | String | 否 | 手机号 |
| department | String | 否 | 部门 |
| position | String | 否 | 职位 |

**请求体示例**:

```json
{
  "username": "newuser",
  "password": "password123",
  "email": "user@example.com",
  "fullName": "新用户",
  "phone": "13800138000",
  "department": "技术部",
  "position": "工程师"
}
```

**响应示例 (201)**:

```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400,
    "user": {
      "id": 2,
      "username": "newuser",
      "email": "user@example.com",
      "fullName": "新用户",
      "phone": "13800138000",
      "department": "技术部",
      "position": "工程师",
      "status": 1
    }
  }
}
```

**错误响应 (400)**:

```json
{
  "code": 400,
  "message": "用户名已存在"
}
```

**CURL 示例**:

```bash
curl -X POST http://localhost:8081/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"newuser","password":"password123","email":"user@example.com","fullName":"新用户"}'
```

---

### 3. 获取当前用户信息

**接口**: `GET /api/v1/auth/me`

**描述**: 获取当前登录用户的详细信息

**请求头**:

| 参数名 | 必填 | 描述 |
|--------|------|------|
| Authorization | 是 | Bearer Token |

**响应示例 (200)**:

```json
{
  "code": 200,
  "data": {
    "id": 1,
    "username": "admin",
    "email": "admin@example.com",
    "fullName": "管理员",
    "phone": "13800138000",
    "department": "技术部",
    "position": "管理员",
    "status": 1
  }
}
```

**错误响应 (401)**:

```json
{
  "code": 401,
  "message": "无效的 token"
}
```

**CURL 示例**:

```bash
curl -X GET http://localhost:8081/api/v1/auth/me \
  -H "Authorization: Bearer <token>"
```

---

### 4. 更新用户信息

**接口**: `PUT /api/v1/users/{id}`

**描述**: 更新指定用户的信息

**路径参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| id | Long | 是 | 用户ID |

**请求头**:

| 参数名 | 必填 | 描述 |
|--------|------|------|
| Authorization | 是 | Bearer Token |

**请求体**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| email | String | 否 | 邮箱 |
| fullName | String | 否 | 真实姓名 |
| phone | String | 否 | 手机号 |
| department | String | 否 | 部门 |
| position | String | 否 | 职位 |

**请求体示例**:

```json
{
  "email": "newemail@example.com",
  "fullName": "新名字",
  "phone": "13900139000",
  "department": "研发部",
  "position": "高级工程师"
}
```

**响应示例 (200)**:

```json
{
  "code": 200,
  "message": "用户更新成功",
  "data": {
    "id": 1,
    "username": "admin",
    "email": "newemail@example.com",
    "fullName": "新名字",
    "phone": "13900139000",
    "department": "研发部",
    "position": "高级工程师",
    "status": 1
  }
}
```

**CURL 示例**:

```bash
curl -X PUT http://localhost:8081/api/v1/users/1 \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"fullName":"新名字","email":"newemail@example.com"}'
```

---

### 5. 获取所有用户

**接口**: `GET /api/v1/users`

**描述**: 获取所有用户列表

**请求头**:

| 参数名 | 必填 | 描述 |
|--------|------|------|
| Authorization | 是 | Bearer Token |

**响应示例 (200)**:

```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "username": "admin",
      "email": "admin@example.com",
      "fullName": "管理员",
      "phone": "13800138000",
      "department": "技术部",
      "position": "管理员",
      "status": 1
    }
  ]
}
```

**CURL 示例**:

```bash
curl -X GET http://localhost:8081/api/v1/users \
  -H "Authorization: Bearer <token>"
```

---

## 权限服务 (Permission Service)

**端口**: 8082  
**基础路径**: `/api/v1`

### 1. 检查权限

**接口**: `GET /api/v1/permissions/check`

**描述**: 检查用户是否具有特定权限

**请求参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| userId | Long | 是 | 用户ID |
| resource | String | 是 | 资源标识 |
| action | String | 是 | 操作类型 |

**响应示例 (200)**:

```json
{
  "code": 200,
  "hasPermission": true
}
```

**CURL 示例**:

```bash
curl -X GET "http://localhost:8082/api/v1/permissions/check?userId=1&resource=document&action=read" \
  -H "Authorization: Bearer <token>"
```

---

### 2. 分配角色

**接口**: `POST /api/v1/permissions/role`

**描述**: 为用户分配角色

**请求参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| userId | Long | 是 | 用户ID |
| role | String | 是 | 角色名称 |

**响应示例 (200)**:

```json
{
  "code": 200,
  "message": "角色分配成功"
}
```

**CURL 示例**:

```bash
curl -X POST "http://localhost:8082/api/v1/permissions/role?userId=1&role=admin" \
  -H "Authorization: Bearer <token>"
```

---

### 3. 撤销角色

**接口**: `DELETE /api/v1/permissions/role`

**描述**: 撤销用户的角色

**请求参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| userId | Long | 是 | 用户ID |
| role | String | 是 | 角色名称 |

**响应示例 (200)**:

```json
{
  "code": 200,
  "message": "角色撤销成功"
}
```

**CURL 示例**:

```bash
curl -X DELETE "http://localhost:8082/api/v1/permissions/role?userId=1&role=admin" \
  -H "Authorization: Bearer <token>"
```

---

## 文档服务 (Document Service)

**端口**: 8083  
**基础路径**: `/api/v1`

### 1. 创建文档

**接口**: `POST /api/v1/documents`

**描述**: 创建新文档

**请求头**:

| 参数名 | 必填 | 描述 |
|--------|------|------|
| Authorization | 是 | Bearer Token |

**请求体**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| title | String | 是 | 文档标题 |
| content | String | 否 | 文档内容 |
| docType | String | 是 | 文档类型 |
| folderId | Long | 否 | 文件夹ID |
| tags | List<String> | 否 | 标签 |

**请求体示例**:

```json
{
  "title": "项目计划书",
  "content": "这是文档内容...",
  "docType": "docx",
  "folderId": 1,
  "tags": ["计划", "重要"]
}
```

**响应示例 (201)**:

```json
{
  "code": 200,
  "message": "文档创建成功",
  "data": {
    "id": 1,
    "title": "项目计划书",
    "content": "这是文档内容...",
    "docType": "docx",
    "creatorId": 1,
    "creatorName": "管理员",
    "folderId": 1,
    "status": "draft",
    "version": 1,
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T10:00:00"
  }
}
```

**CURL 示例**:

```bash
curl -X POST http://localhost:8083/api/v1/documents \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"title":"项目计划书","content":"内容","docType":"docx"}'
```

---

### 2. 获取文档详情

**接口**: `GET /api/v1/documents/{id}`

**描述**: 获取指定文档的详细信息

**路径参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| id | Long | 是 | 文档ID |

**响应示例 (200)**:

```json
{
  "code": 200,
  "data": {
    "id": 1,
    "title": "项目计划书",
    "content": "这是文档内容...",
    "docType": "docx",
    "creatorId": 1,
    "creatorName": "管理员",
    "folderId": 1,
    "status": "draft",
    "version": 1,
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T10:00:00"
  }
}
```

**CURL 示例**:

```bash
curl -X GET http://localhost:8083/api/v1/documents/1 \
  -H "Authorization: Bearer <token>"
```

---

### 3. 更新文档

**接口**: `PUT /api/v1/documents/{id}`

**描述**: 更新指定文档的内容

**路径参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| id | Long | 是 | 文档ID |

**请求体**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| title | String | 否 | 文档标题 |
| content | String | 否 | 文档内容 |
| docType | String | 否 | 文档类型 |
| status | String | 否 | 状态 |

**响应示例 (200)**:

```json
{
  "code": 200,
  "message": "文档更新成功",
  "data": {
    "id": 1,
    "title": "更新后的标题",
    "content": "更新后的内容...",
    "version": 2,
    "updatedAt": "2024-01-01T11:00:00"
  }
}
```

**CURL 示例**:

```bash
curl -X PUT http://localhost:8083/api/v1/documents/1 \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"title":"更新后的标题","content":"新内容"}'
```

---

### 4. 删除文档

**接口**: `DELETE /api/v1/documents/{id}`

**描述**: 删除指定文档

**路径参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| id | Long | 是 | 文档ID |

**响应示例 (200)**:

```json
{
  "code": 200,
  "message": "文档删除成功"
}
```

**错误响应 (404)**:

```json
{
  "code": 404,
  "message": "文档不存在"
}
```

**CURL 示例**:

```bash
curl -X DELETE http://localhost:8083/api/v1/documents/1 \
  -H "Authorization: Bearer <token>"
```

---

### 5. 获取文档列表

**接口**: `GET /api/v1/documents`

**描述**: 获取所有文档列表

**响应示例 (200)**:

```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "title": "项目计划书",
      "docType": "docx",
      "creatorId": 1,
      "creatorName": "管理员",
      "status": "published",
      "version": 1
    }
  ]
}
```

**CURL 示例**:

```bash
curl -X GET http://localhost:8083/api/v1/documents \
  -H "Authorization: Bearer <token>"
```

---

### 6. 分页获取文档

**接口**: `GET /api/v1/documents/page`

**描述**: 分页获取文档列表

**请求参数**:

| 参数名 | 类型 | 必填 | 默认值 | 描述 |
|--------|------|------|--------|------|
| pageNum | int | 否 | 1 | 页码 |
| pageSize | int | 否 | 10 | 每页数量 |

**响应示例 (200)**:

```json
{
  "code": 200,
  "data": {
    "records": [],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  }
}
```

**CURL 示例**:

```bash
curl -X GET "http://localhost:8083/api/v1/documents/page?pageNum=1&pageSize=10" \
  -H "Authorization: Bearer <token>"
```

---

## 工作流服务 (Workflow Service)

**端口**: 8084  
**基础路径**: `/api/v1`

### 1. 创建审批流程

**接口**: `POST /api/v1/workflows`

**描述**: 创建一个新的审批流程

**请求头**:

| 参数名 | 必填 | 描述 |
|--------|------|------|
| Authorization | 是 | Bearer Token |

**请求体**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| documentId | Long | 是 | 文档ID |
| title | String | 是 | 流程标题 |
| workflowType | String | 是 | 流程类型 |
| approvers | List<Long> | 是 | 审批人ID列表 |
| content | String | 否 | 审批内容 |

**请求体示例**:

```json
{
  "documentId": 1,
  "title": "文档审批",
  "workflowType": "document_approval",
  "approvers": [2, 3],
  "content": "请审批此文档"
}
```

**响应示例 (200)**:

```json
{
  "code": 200,
  "message": "审批流程创建成功",
  "data": {
    "id": 1,
    "documentId": 1,
    "title": "文档审批",
    "workflowType": "document_approval",
    "status": "pending",
    "currentApprover": 2,
    "createdBy": 1,
    "createdAt": "2024-01-01T10:00:00"
  }
}
```

**CURL 示例**:

```bash
curl -X POST http://localhost:8084/api/v1/workflows \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"documentId":1,"title":"文档审批","workflowType":"document_approval","approvers":[2,3]}'
```

---

### 2. 获取审批流程列表

**接口**: `GET /api/v1/workflows`

**描述**: 获取审批流程列表，支持多种过滤条件

**请求参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| docId | Long | 否 | 文档ID |
| userId | Long | 否 | 用户ID（获取待审批任务） |
| page | int | 否 | 页码 |
| size | int | 否 | 每页数量 |
| status | String | 否 | 流程状态 |

**响应示例 (200)**:

```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "documentId": 1,
      "title": "文档审批",
      "status": "pending",
      "createdBy": 1,
      "createdAt": "2024-01-01T10:00:00"
    }
  ]
}
```

**CURL 示例**:

```bash
curl -X GET "http://localhost:8084/api/v1/workflows?userId=1" \
  -H "Authorization: Bearer <token>"
```

---

### 3. 审批通过

**接口**: `PUT /api/v1/workflows/{id}/approve`

**描述**: 审批通过指定的审批流程

**路径参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| id | Long | 是 | 流程ID |

**请求体**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| taskId | Long | 是 | 任务ID |
| approverId | Long | 是 | 审批人ID |
| comment | String | 否 | 审批意见 |

**请求体示例**:

```json
{
  "taskId": 1,
  "approverId": 2,
  "comment": "同意发布"
}
```

**响应示例 (200)**:

```json
{
  "code": 200,
  "message": "审批通过",
  "data": {
    "id": 1,
    "status": "approved",
    "updatedAt": "2024-01-01T11:00:00"
  }
}
```

**错误响应 (400)**:

```json
{
  "code": 400,
  "message": "审批失败: 流程不存在"
}
```

**CURL 示例**:

```bash
curl -X PUT http://localhost:8084/api/v1/workflows/1/approve \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"taskId":1,"approverId":2,"comment":"同意"}'
```

---

### 4. 审批拒绝

**接口**: `PUT /api/v1/workflows/{id}/reject`

**描述**: 拒绝指定的审批流程

**路径参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| id | Long | 是 | 流程ID |

**请求体**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| taskId | Long | 是 | 任务ID |
| approverId | Long | 是 | 审批人ID |
| comment | String | 否 | 拒绝原因 |

**响应示例 (200)**:

```json
{
  "code": 200,
  "message": "审批已拒绝",
  "data": {
    "id": 1,
    "status": "rejected",
    "updatedAt": "2024-01-01T11:00:00"
  }
}
```

**CURL 示例**:

```bash
curl -X PUT http://localhost:8084/api/v1/workflows/1/reject \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"taskId":1,"approverId":2,"comment":"需要修改"}'
```

---

## 通知服务 (Notification Service)

**端口**: 8085  
**基础路径**: `/api/v1`

### 1. 获取通知列表

**接口**: `GET /api/v1/notifications`

**描述**: 获取当前用户的消息通知列表

**请求参数**:

| 参数名 | 类型 | 必填 | 默认值 | 描述 |
|--------|------|------|--------|------|
| userId | Long | 是 | - | 用户ID |
| page | int | 否 | 1 | 页码 |
| size | int | 否 | 20 | 每页数量 |

**响应示例 (200)**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "records": [
      {
        "id": 1,
        "userId": 1,
        "type": "workflow",
        "title": "审批通知",
        "content": "您有一个待审批的文档",
        "isRead": false,
        "createdAt": "2024-01-01T10:00:00"
      }
    ],
    "total": 10,
    "size": 20,
    "current": 1
  }
}
```

**CURL 示例**:

```bash
curl -X GET "http://localhost:8085/api/v1/notifications?userId=1&page=1&size=20" \
  -H "Authorization: Bearer <token>"
```

---

### 2. 获取未读数量

**接口**: `GET /api/v1/notifications/unread`

**描述**: 获取用户未读通知数量

**请求参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| userId | Long | 是 | 用户ID |

**响应示例 (200)**:

```json
{
  "code": 200,
  "message": "获取成功",
  "data": 5
}
```

**CURL 示例**:

```bash
curl -X GET "http://localhost:8085/api/v1/notifications/unread?userId=1" \
  -H "Authorization: Bearer <token>"
```

---

### 3. 标记已读

**接口**: `PUT /api/v1/notifications/{id}/read`

**描述**: 将指定通知标记为已读

**路径参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| id | Long | 是 | 通知ID |

**请求参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| userId | Long | 是 | 用户ID |

**响应示例 (200)**:

```json
{
  "code": 200,
  "message": "标记已读成功"
}
```

**错误响应 (404)**:

```json
{
  "code": 404,
  "message": "通知不存在"
}
```

**CURL 示例**:

```bash
curl -X PUT "http://localhost:8085/api/v1/notifications/1/read?userId=1" \
  -H "Authorization: Bearer <token>"
```

---

### 4. 全部标记已读

**接口**: `PUT /api/v1/notifications/read-all`

**描述**: 将用户所有通知标记为已读

**请求参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| userId | Long | 是 | 用户ID |

**响应示例 (200)**:

```json
{
  "code": 200,
  "message": "全部标记已读成功",
  "data": 5
}
```

**CURL 示例**:

```bash
curl -X PUT "http://localhost:8085/api/v1/notifications/read-all?userId=1" \
  -H "Authorization: Bearer <token>"
```

---

## 协作服务 (Collab Service)

**端口**: 8086  
**基础路径**: `/api/v1`

### 1. 添加评论

**接口**: `POST /api/v1/comments`

**描述**: 为文档添加评论

**请求头**:

| 参数名 | 必填 | 描述 |
|--------|------|------|
| Authorization | 是 | Bearer Token |

**请求体**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| documentId | Long | 是 | 文档ID |
| content | String | 是 | 评论内容 |
| authorId | Long | 是 | 评论人ID |
| authorName | String | 是 | 评论人名称 |

**请求体示例**:

```json
{
  "documentId": 1,
  "content": "这篇文章写得很好",
  "authorId": 1,
  "authorName": "管理员"
}
```

**响应示例 (201)**:

```json
{
  "id": 1,
  "documentId": 1,
  "content": "这篇文章写得很好",
  "authorId": 1,
  "authorName": "管理员",
  "createdAt": "2024-01-01T10:00:00"
}
```

**CURL 示例**:

```bash
curl -X POST http://localhost:8086/api/v1/comments \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"documentId":1,"content":"写得不错","authorId":1,"authorName":"管理员"}'
```

---

### 2. 获取评论列表

**接口**: `GET /api/v1/comments`

**描述**: 获取指定文档的所有评论

**请求参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| docId | Long | 是 | 文档ID |

**响应示例 (200)**:

```json
[
  {
    "id": 1,
    "documentId": 1,
    "content": "这篇文章写得很好",
    "authorId": 1,
    "authorName": "管理员",
    "createdAt": "2024-01-01T10:00:00"
  }
]
```

**CURL 示例**:

```bash
curl -X GET "http://localhost:8086/api/v1/comments?docId=1" \
  -H "Authorization: Bearer <token>"
```

---

### 3. 添加批注

**接口**: `POST /api/v1/annotations`

**描述**: 为文档添加批注

**请求头**:

| 参数名 | 必填 | 描述 |
|--------|------|------|
| Authorization | 是 | Bearer Token |

**请求体**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| documentId | Long | 是 | 文档ID |
| content | String | 是 | 批注内容 |
| position | Object | 是 | 位置信息 |
| authorId | Long | 是 | 批注人ID |
| authorName | String | 是 | 批注人名称 |

**请求体示例**:

```json
{
  "documentId": 1,
  "content": "此处需要补充说明",
  "position": {
    "page": 1,
    "x": 100,
    "y": 200
  },
  "authorId": 1,
  "authorName": "管理员"
}
```

**响应示例 (201)**:

```json
{
  "id": 1,
  "documentId": 1,
  "content": "此处需要补充说明",
  "position": {
    "page": 1,
    "x": 100,
    "y": 200
  },
  "authorId": 1,
  "authorName": "管理员",
  "createdAt": "2024-01-01T10:00:00"
}
```

**CURL 示例**:

```bash
curl -X POST http://localhost:8086/api/v1/annotations \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"documentId":1,"content":"批注内容","position":{"page":1,"x":100,"y":200},"authorId":1,"authorName":"管理员"}'
```

---

### 4. 获取批注列表

**接口**: `GET /api/v1/annotations`

**描述**: 获取指定文档的所有批注

**请求参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| docId | Long | 是 | 文档ID |

**响应示例 (200)**:

```json
[
  {
    "id": 1,
    "documentId": 1,
    "content": "此处需要补充说明",
    "position": {
      "page": 1,
      "x": 100,
      "y": 200
    },
    "authorId": 1,
    "authorName": "管理员",
    "createdAt": "2024-01-01T10:00:00"
  }
]
```

**CURL 示例**:

```bash
curl -X GET "http://localhost:8086/api/v1/annotations?docId=1" \
  -H "Authorization: Bearer <token>"
```

---

## 搜索服务 (Search Service)

**端口**: 8087  
**基础路径**: `/api/v1`

### 1. 全文搜索

**接口**: `GET /api/v1/search`

**描述**: 全文搜索文档

**请求参数**:

| 参数名 | 类型 | 必填 | 默认值 | 描述 |
|--------|------|------|--------|------|
| q | String | 否 | - | 搜索关键词 |
| docType | String | 否 | - | 文档类型 |
| creatorId | Long | 否 | - | 创建者ID |
| folderId | Long | 否 | - | 文件夹ID |
| status | String | 否 | - | 文档状态 |
| pageNum | Integer | 否 | 1 | 页码 |
| pageSize | Integer | 否 | 10 | 每页数量 |

**响应示例 (200)**:

```json
{
  "code": 200,
  "message": "搜索成功",
  "data": [
    {
      "id": 1,
      "title": "项目计划书",
      "content": "这是文档内容摘要...",
      "docType": "docx",
      "score": 0.95
    }
  ]
}
```

**CURL 示例**:

```bash
curl -X GET "http://localhost:8087/api/v1/search?q=计划书&docType=docx&pageNum=1&pageSize=10" \
  -H "Authorization: Bearer <token>"
```

---

### 2. 重建索引

**接口**: `POST /api/v1/search/index`

**描述**: 重建搜索索引

**响应示例 (202)**:

```json
{
  "code": 200,
  "message": "索引重建成功"
}
```

**CURL 示例**:

```bash
curl -X POST http://localhost:8087/api/v1/search/index \
  -H "Authorization: Bearer <token>"
```

---

## 存储服务 (Storage Service)

**端口**: 8088  
**基础路径**: `/api/v1`

### 1. 文件上传

**接口**: `POST /api/v1/storage/upload`

**描述**: 上传文件到存储服务

**请求参数 (Multipart)**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| file | MultipartFile | 是 | 上传的文件 |
| documentId | Long | 否 | 关联文档ID |
| customFileName | String | 否 | 自定义文件名 |
| uploadUserId | Long | 否 | 上传用户ID |
| uploadUserName | String | 否 | 上传用户名 |

**响应示例 (200)**:

```json
{
  "fileId": 1,
  "fileName": "document.pdf",
  "fileSize": 1024000,
  "contentType": "application/pdf",
  "uploadUrl": "/api/v1/storage/1",
  "createdAt": "2024-01-01T10:00:00"
}
```

**CURL 示例**:

```bash
curl -X POST http://localhost:8088/api/v1/storage/upload \
  -H "Authorization: Bearer <token>" \
  -F "file=@/path/to/file.pdf" \
  -F "documentId=1" \
  -F "uploadUserId=1"
```

---

### 2. 获取文件信息

**接口**: `GET /api/v1/storage/{fileId}`

**描述**: 获取文件详细信息

**路径参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| fileId | Long | 是 | 文件ID |

**响应示例 (200)**:

```json
{
  "id": 1,
  "fileName": "document.pdf",
  "fileSize": 1024000,
  "contentType": "application/pdf",
  "uploadUserId": 1,
  "createdAt": "2024-01-01T10:00:00"
}
```

**错误响应 (404)**:

```json
{
  "code": 404,
  "message": "文件不存在"
}
```

**CURL 示例**:

```bash
curl -X GET http://localhost:8088/api/v1/storage/1 \
  -H "Authorization: Bearer <token>"
```

---

### 3. 获取下载链接

**接口**: `GET /api/v1/storage/{fileId}/download`

**描述**: 获取文件下载链接

**路径参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| fileId | Long | 是 | 文件ID |

**请求参数**:

| 参数名 | 类型 | 必填 | 默认值 | 描述 |
|--------|------|------|--------|------|
| expiry | int | 否 | 60 | 链接有效期(分钟) |

**响应示例 (200)**:

```json
{
  "url": "https://storage.example.com/files/1/download?token=xxx",
  "expiresIn": "60 minutes"
}
```

**CURL 示例**:

```bash
curl -X GET "http://localhost:8088/api/v1/storage/1/download?expiry=60" \
  -H "Authorization: Bearer <token>"
```

---

### 4. 删除文件

**接口**: `DELETE /api/v1/storage/{fileId}`

**描述**: 删除指定文件

**路径参数**:

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| fileId | Long | 是 | 文件ID |

**响应示例 (200)**:

```json
{
  "code": 200,
  "message": "文件删除成功"
}
```

**CURL 示例**:

```bash
curl -X DELETE http://localhost:8088/api/v1/storage/1 \
  -H "Authorization: Bearer <token>"
```

---

## 错误码说明

| 错误码 | 描述 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权/Token无效 |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |