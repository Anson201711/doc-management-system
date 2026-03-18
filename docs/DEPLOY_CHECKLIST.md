# 部署检查清单 (DEPLOY CHECKLIST)

## 部署前检查

### 1. 环境检查
- [ ] 目标服务器操作系统确认 (Ubuntu 20.04+ / CentOS 8+ / Rocky Linux 9)
- [ ] 服务器资源配置: CPU ≥ 4核, 内存 ≥ 8GB, 磁盘 ≥ 100GB
- [ ] 网络端口开放: 3306, 6379, 9200, 8848, 9000, 9001, 8080

### 2. Docker 环境
- [ ] Docker 24.0+ 已安装
- [ ] Docker Compose v2.24+ 已安装
- [ ] Docker 服务已启动

### 3. 代码完整性
- [ ] 后端微服务目录完整 (9个服务)
- [ ] 前端代码完整 (frontend/dist 已构建)
- [ ] Dockerfile 完整
- [ ] docker-compose.yml 完整

---

## 部署任务清单

### DEPLOY-01: Docker中间件部署
| 序号 | 服务 | 端口 | 状态 | 备注 |
|------|------|------|------|------|
| 1 | MySQL 8.0 | 3306 | ⬜ | |
| 2 | Redis 7 | 6379 | ⬜ | |
| 3 | Elasticsearch 8 | 9200 | ⬜ | |
| 4 | Nacos 2.2.3 | 8848 | ⬜ | |
| 5 | MinIO | 9000/9001 | ⬜ | |

### DEPLOY-02: Spring Cloud 微服务构建部署
| 序号 | 服务 | 端口 | 状态 | 备注 |
|------|------|------|------|------|
| 1 | user-service | 8081 | ⬜ | |
| 2 | document-service | 8083 | ⬜ | |
| 3 | permission-service | 8082 | ⬜ | |
| 4 | workflow-service | 8084 | ⬜ | |
| 5 | notification-service | 8085 | ⬜ | |
| 6 | search-service | 8086 | ⬜ | |
| 7 | storage-service | 8087 | ⬜ | |
| 8 | api-gateway | 8080 | ⬜ | |
| 9 | collab-service | 8088 | ⬜ | |

### DEPLOY-03: 前端构建部署
| 序号 | 任务 | 状态 | 备注 |
|------|------|------|------|
| 1 | React 18 构建 | ⬜ | npm run build |
| 2 | Nginx 部署 | ⬜ | 端口 80/443 |

### DEPLOY-04: Nacos 服务注册配置
| 序号 | 任务 | 状态 | 备注 |
|------|------|------|------|
| 1 | 服务注册 | ⬜ | 9个微服务 |
| 2 | 配置管理 | ⬜ | application.yml |
| 3 | 命名空间 | ⬜ | docman |

### DEPLOY-05: Nginx 反向代理配置
| 序号 | 任务 | 状态 | 备注 |
|------|------|------|------|
| 1 | 静态资源 | ⬜ | / -> frontend |
| 2 | API 代理 | ⬜ | /api -> gateway |
| 3 | WebSocket | ⬜ | collab-service |

### DEPLOY-06: 部署验证
| 序号 | 检查项 | 状态 | 备注 |
|------|--------|------|------|
| 1 | 容器运行状态 | ⬜ | docker ps |
| 2 | 前端可访问 | ⬜ | http://localhost |
| 3 | 后端API健康 | ⬜ | /health |
| 4 | 数据库连接 | ⬜ | MySQL |
| 5 | 缓存连接 | ⬜ | Redis |
| 6 | 搜索服务 | ⬜ | ES |
| 7 | 对象存储 | ⬜ | MinIO |
| 8 | 服务注册 | ⬜ | Nacos |

---

## 部署顺序

```
1. 环境准备
   └─> 安装 Docker

2. DEPLOY-01: 基础设施
   ├─> MySQL 8.0
   ├─> Redis 7
   ├─> Elasticsearch 8
   ├─> Nacos 2.2.3
   └─> MinIO

3. DEPLOY-02: 微服务构建部署
   ├─> 构建 (Maven)
   ├─> Docker 镜像构建
   └─> 容器启动

4. DEPLOY-03: 前端部署
   ├─> npm build
   └─> Nginx 部署

5. DEPLOY-04: Nacos 配置
   ├─> 服务注册
   └─> 配置同步

6. DEPLOY-05: Nginx 配置
   └─> 反向代理生效

7. DEPLOY-06: 验证
   └─> 全链路测试
```

---

## 快速命令参考

```bash
# 1. 启动基础设施
cd docker
docker-compose -f docker-compose.infra.yml up -d

# 2. 启动全部服务
docker-compose up -d

# 3. 查看状态
docker-compose ps

# 4. 查看日志
docker-compose logs -f

# 5. 停止
docker-compose down
```