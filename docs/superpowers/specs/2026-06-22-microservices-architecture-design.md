# Spring Cloud 微服务架构设计文档

## 1. 项目概述

### 1.1 背景

当前项目是一个单体 Spring Boot 应用，集成了 Nacos、PostgreSQL、Kafka、Redis，但尚未实现真正的微服务架构。本文档规划从单体演进为微服务的完整架构。

### 1.2 学习目标

掌握 Spring Cloud 微服务核心概念：
- **服务注册与发现** — Nacos
- **配置中心** — Nacos Config
- **API 网关** — Spring Cloud Gateway
- **服务间调用** — OpenFeign
- **熔断降级** — Sentinel
- **分布式链路追踪** — Micrometer Tracing + Zipkin
- **异步消息** — Kafka 事件驱动
- **JWT 认证** — 网关统一鉴权

### 1.3 业务场景

社交/内容平台，包含用户注册登录、内容发布、评论、消息通知等核心功能。

---

## 2. 整体架构

```
                          ┌─────────────────┐
                          │   客户端/Web     │
                          └────────┬────────┘
                                   │
                          ┌────────▼────────┐
                          │  Gateway Service │  :8080
                          │  (路由/限流/CORS) │
                          └────────┬────────┘
                                   │
            ┌──────────────────────┼──────────────────────┐
            │                      │                      │
   ┌────────▼────────┐  ┌─────────▼────────┐  ┌─────────▼────────┐
   │   User Service   │  │ Content Service  │  │ Comment Service  │
   │      :8081       │  │      :8082       │  │      :8083       │
   └────────┬────────┘  └─────────┬────────┘  └─────────┬────────┘
            │                      │                      │
            │            ┌─────────▼────────┐             │
            │            │ Notification Svc │             │
            │            │      :8084       │             │
            │            └──────────────────┘             │
            │                      ▲                      │
            │                      │ Kafka                 │
            │                      │                      │
   ┌────────▼──────────────────────▼──────────────────────▼────────┐
   │                      基础设施层                                │
   │  Nacos(注册/配置)  PostgreSQL  Redis  Kafka  Zipkin(链路追踪)  │
   └───────────────────────────────────────────────────────────────┘
```

### 2.1 服务间通信

| 调用方 | 被调用方 | 方式 | 场景 |
|--------|----------|------|------|
| content-service | user-service | OpenFeign (同步) | 创建帖子时验证用户是否存在 |
| comment-service | content-service | OpenFeign (同步) | 创建评论时验证帖子是否存在 |
| content-service | notification-service | Kafka (异步) | 帖子发布/删除事件 |
| comment-service | notification-service | Kafka (异步) | 评论新增事件 |

---

## 3. 服务拆分详情

### 3.1 Gateway Service（:8080）

**职责**：统一入口，路由转发，限流，跨域处理，JWT 鉴权

**技术栈**：
- Spring Cloud Gateway
- Sentinel 限流
- Nacos 服务发现
- JWT Token 校验（Global Filter）

**路由规则**：
```
/api/auth/**           → user-service       (白名单，不校验 Token)
/api/users/**          → user-service       (需 Token)
/api/content/**        → content-service    (需 Token)
/api/comments/**       → comment-service    (需 Token)
/api/notifications/**  → notification-service (需 Token)
```

**JWT 鉴权过滤器**：
- 白名单路径（`/api/auth/register`, `/api/auth/login`）直接放行
- 其他路径校验 `Authorization: Bearer <token>` 头
- 校验通过 → 解析 `userId`、`username` 写入请求头转发给下游
- 校验失败 → 返回 `401 Unauthorized`

**不包含**：业务逻辑、数据库连接

---

### 3.2 User Service（:8081）

**职责**：用户注册登录、JWT 签发、用户管理

**基于现有代码**：复用当前 `UserController`、`UserService`、`UserMapper`、`User` 实体

**数据库**：`user_db`（PostgreSQL，独立 schema）

**User 实体扩展**：
```java
public class User {
    private Long id;
    private String username;    // 登录用户名（新增）
    private String password;    // 密码 BCrypt 加密（新增）
    private String name;        // 显示名称
    private String email;
    private String phone;
}
```

**对外接口**：
```
POST   /api/auth/register   — 用户注册（用户名+密码+邮箱）
POST   /api/auth/login      — 用户登录（返回 JWT Token）
GET    /api/auth/me          — 获取当前用户信息（需 Token）
GET    /api/users            — 查询所有用户
GET    /api/users/{id}       — 根据 ID 查询
PUT    /api/users/{id}       — 更新用户
DELETE /api/users/{id}       — 删除用户
```

**JWT 签发**：登录成功后生成 Token，包含 `userId` 和 `username`，有效期 24 小时

**Nacos 注册名**：`user-service`

---

### 3.3 Content Service（:8082）

**职责**：帖子/内容的发布、查询、更新、删除

**数据库**：`content_db`（PostgreSQL，独立 schema）

**实体设计**：
```java
public class Content {
    private Long id;           // 帖子 ID
    private Long userId;       // 作者 ID（关联 user-service）
    private String title;      // 标题
    private String body;       // 正文
    private String status;     // DRAFT/PUBLISHED/DELETED
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

**对外接口**：
```
POST   /api/content            — 发布帖子
GET    /api/content             — 查询帖子列表（支持分页）
GET    /api/content/{id}        — 查询单篇帖子
GET    /api/content/user/{userId} — 查询某用户的帖子
PUT    /api/content/{id}        — 更新帖子
DELETE /api/content/{id}        — 删除帖子
```

**Feign 调用**：创建帖子时调用 `user-service` 验证用户存在

**Kafka 事件**：帖子发布/删除时发送 `content-events` Topic

**Nacos 注册名**：`content-service`

---

### 3.4 Comment Service（:8083）

**职责**：评论的增删改查

**数据库**：`comment_db`（PostgreSQL，独立 schema）

**实体设计**：
```java
public class Comment {
    private Long id;           // 评论 ID
    private Long contentId;    // 关联帖子 ID（关联 content-service）
    private Long userId;       // 评论者 ID（关联 user-service）
    private String body;       // 评论内容
    private LocalDateTime createdAt;
}
```

**对外接口**：
```
POST   /api/comments                    — 新增评论
GET    /api/comments/content/{contentId} — 查询某帖子的评论
DELETE /api/comments/{id}                — 删除评论
```

**Feign 调用**：创建评论时调用 `content-service` 验证帖子存在

**Kafka 事件**：新增评论时发送 `comment-events` Topic

**Nacos 注册名**：`comment-service`

---

### 3.5 Notification Service（:8084）

**职责**：消费 Kafka 事件，生成通知消息

**数据库**：`notification_db`（PostgreSQL，独立 schema）

**实体设计**：
```java
public class Notification {
    private Long id;
    private Long userId;       // 接收通知的用户
    private String type;       // CONTENT_PUBLISHED/COMMENT_ADDED
    private String message;    // 通知内容
    private boolean read;      // 是否已读
    private LocalDateTime createdAt;
}
```

**对外接口**：
```
GET    /api/notifications/user/{userId} — 查询用户的通知
PUT    /api/notifications/{id}/read     — 标记已读
```

**Kafka 消费**：
- 监听 `content-events` → 生成"XX 发布了新帖子"通知
- 监听 `comment-events` → 生成"XX 评论了你的帖子"通知

**Nacos 注册名**：`notification-service`

---

## 4. 基础设施配置

### 4.1 Nacos

| 配置项 | 值 |
|--------|-----|
| 服务地址 | localhost:8848 |
| 命名空间 | public |
| 分组 | DEFAULT_GROUP |
| 配置文件格式 | yml |

每个微服务在 Nacos 注册自己的服务名，通过 `lb://service-name` 实现负载均衡调用。

### 4.2 PostgreSQL

每个服务独立数据库，实现数据隔离：

| 服务 | 数据库名 | 端口 |
|------|----------|------|
| user-service | user_db | 5432 |
| content-service | content_db | 5432 |
| comment-service | comment_db | 5432 |
| notification-service | notification_db | 5432 |

所有数据库在同一 PostgreSQL 实例上，通过不同的 database name 隔离。

### 4.3 Kafka

| 配置项 | 值 |
|--------|-----|
| 地址 | localhost:9092 |
| content-events Topic | content-service 生产，notification-service 消费 |
| comment-events Topic | comment-service 生产，notification-service 消费 |

### 4.4 Redis

| 配置项 | 值 |
|--------|-----|
| 地址 | localhost:6379 |
| 用途 | 各服务可选用 Redis 缓存热点数据 |

### 4.5 分布式链路追踪

| 组件 | 用途 |
|------|------|
| Micrometer Tracing | Spring Boot 3.x 内置的链路追踪抽象 |
| Brave | Micrometer 的 Tracing 实现 |
| Zipkin | 链路追踪数据收集和展示 |

每个服务引入 `micrometer-tracing-bridge-brave` 和 `zipkin-reporter-brave`，自动上报链路数据到 Zipkin。

---

## 5. 公共模块（common-module）

提取各服务共用的代码，减少重复：

```
common-module/
├── exception/
│   ├── BusinessException.java
│   ├── ResourceNotFoundException.java
│   └── GlobalExceptionHandler.java
├── dto/
│   └── ApiResponse.java              # 统一响应格式
├── auth/
│   └── JwtUtil.java                  # JWT 工具类（签发/解析/校验）
└── config/
    └── NacosConfig.java              # 公共 Nacos 配置
```

common-module 作为 Maven 依赖引入各服务，不独立部署。

---

## 6. 学习路径（实施顺序）

按照由简到繁的顺序逐步搭建：

| 阶段 | 内容 | 学习重点 |
|------|------|----------|
| 阶段 1 | 搭建多模块骨架 + user-service 迁移 | Maven 多模块、Nacos 注册 |
| 阶段 2 | 新增 content-service + OpenFeign 调用 | 服务间同步调用、Feign |
| 阶段 3 | 新增 Gateway Service + JWT 鉴权 | API 网关、路由、JWT 认证 |
| 阶段 4 | 新增 comment-service + Kafka 事件 | 异步消息、事件驱动 |
| 阶段 5 | 新增 notification-service | Kafka 消费者、事件处理 |
| 阶段 6 | 集成 Sentinel 熔断降级 | 服务容错、fallback |
| 阶段 7 | 集成链路追踪（Micrometer + Zipkin） | 分布式追踪、可观测性 |
| 阶段 8 | 前端 Vue 3 联调（7 个页面） | 前后端分离、JWT 传递 |

---

## 7. Maven 多模块结构

```xml
<!-- 父 POM -->
<modules>
    <module>common-module</module>
    <module>gateway-service</module>
    <module>user-service</module>
    <module>content-service</module>
    <module>comment-service</module>
    <module>notification-service</module>
</modules>
```

**依赖版本统一管理**：父 POM 的 `<dependencyManagement>` 统一管理 Spring Cloud、Spring Cloud Alibaba、MyBatis-Plus 等版本。

**各服务 POM**：只声明需要的依赖，不指定版本号。

---

## 8. 端口分配

| 服务 | 端口 |
|------|------|
| gateway-service | 8080 |
| user-service | 8081 |
| content-service | 8082 |
| comment-service | 8083 |
| notification-service | 8084 |
| Zipkin | 9411 |
| Nacos | 8848 |
| PostgreSQL | 5432 |
| Kafka | 9092 |
| Redis | 6379 |

---

## 9. 前端设计（Vue 3 + Element Plus）

### 9.1 技术栈

| 组件 | 用途 |
|------|------|
| Vue 3 | 前端框架 |
| Element Plus | UI 组件库 |
| Vue Router | 前端路由 |
| Axios | HTTP 请求 |
| Pinia | 状态管理（存储用户信息和 Token） |

### 9.2 前端项目结构

```
frontend/
├── src/
│   ├── views/
│   │   ├── Login.vue            # 登录页
│   │   ├── Register.vue         # 注册页
│   │   ├── PostList.vue         # 帖子列表（首页）
│   │   ├── PostForm.vue         # 发帖/编辑帖子
│   │   ├── PostDetail.vue       # 帖子详情 + 评论
│   │   ├── UserManage.vue       # 用户管理
│   │   └── Notification.vue     # 通知中心
│   ├── router/
│   │   └── index.js             # 路由配置
│   ├── stores/
│   │   └── user.js              # Pinia 用户状态
│   ├── api/
│   │   ├── request.js           # Axios 实例（拦截器）
│   │   ├── auth.js              # 认证 API
│   │   ├── user.js              # 用户 API
│   │   ├── content.js           # 帖子 API
│   │   ├── comment.js           # 评论 API
│   │   └── notification.js      # 通知 API
│   ├── App.vue
│   └── main.js
├── package.json
└── vite.config.js               # Vite 配置（代理 /api → Gateway）
```

### 9.3 页面设计

#### 登录页（Login.vue）
- 用户名 + 密码输入框 + 登录按钮
- 底部"没有账号？去注册"链接
- 登录成功 → Token 存入 localStorage + Pinia → 跳转首页

#### 注册页（Register.vue）
- 用户名 + 密码 + 确认密码 + 邮箱 + 注册按钮
- 注册成功 → 跳转登录页

#### 帖子列表 — 首页（PostList.vue）
- 顶部导航栏：Logo + "发帖"按钮 + 通知铃铛（未读数）+ 当前用户名 + 退出
- 帖子卡片列表：标题、作者名、发布时间、内容摘要
- 分页组件
- 点击卡片 → 帖子详情页，点击作者 → 该用户帖子列表

#### 发帖/编辑帖子（PostForm.vue）
- 标题输入 + 正文输入 + 作者下拉选择 + 提交按钮
- 编辑模式回填已有数据

#### 帖子详情 + 评论（PostDetail.vue）
- 帖子内容展示区（标题、正文、作者、时间）
- 评论列表（评论者、内容、时间、删除按钮）
- 发表评论区（选择评论者 + 评论内容 + 提交）

#### 用户管理（UserManage.vue）
- 用户表格（ID、姓名、邮箱、手机号、操作列）
- 新增/编辑用户对话框
- 删除确认

#### 通知中心（Notification.vue）
- 用户切换下拉
- 通知列表（类型图标、内容、时间、已读/未读状态）
- 点击通知 → 标记已读

### 9.4 全局机制

**Axios 拦截器**：
```javascript
// 请求拦截器：自动带 Token
headers['Authorization'] = `Bearer ${token}`

// 响应拦截器：401 跳转登录
if (response.status === 401) → router.push('/login')
```

**Vite 代理配置**：
```javascript
proxy: {
  '/api': {
    target: 'http://localhost:8080',  // Gateway 地址
    changeOrigin: true
  }
}
```

### 9.5 页面与接口映射

| 页面 | 调用接口 |
|------|----------|
| 登录页 | `POST /api/auth/login` |
| 注册页 | `POST /api/auth/register` |
| 帖子列表 | `GET /api/content`, `GET /api/users` |
| 发帖/编辑 | `POST /api/content`, `PUT /api/content/{id}`, `GET /api/users` |
| 帖子详情 | `GET /api/content/{id}`, `GET/POST/DELETE /api/comments` |
| 用户管理 | `GET/POST/PUT/DELETE /api/users` |
| 通知中心 | `GET /api/notifications/user/{userId}`, `PUT /api/notifications/{id}/read` |
