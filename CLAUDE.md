# MySpringCloudDemo

基于 JDK 21 + Spring Cloud 的微服务示例项目，集成了 Nacos、PostgreSQL、Kafka、Redis。

## 编码规范

本项目遵循《阿里巴巴Java开发手册》规范，主要包含以下几点：

### 异常处理
- 禁止直接抛出 `RuntimeException`，必须使用自定义业务异常
- 所有业务异常继承自 `BusinessException`
- 使用 `GlobalExceptionHandler` 统一处理异常并返回标准响应

### 日志规范
- Service 层使用 `@Slf4j` 注解
- 创建/更新/删除操作使用 `log.info` 记录
- 查询操作使用 `log.debug` 记录
- 异常使用 `log.error` 记录堆栈信息

### 注释规范
- 所有类必须添加类注释（说明、作者）
- 所有公共方法必须添加 JavaDoc（说明、参数、返回值）
- 常量必须添加注释说明用途

### 基本类型
- 返回布尔值使用基本类型 `boolean`，避免包装类型 `Boolean` 自动拆箱 NPE
- 使用 `Boolean.TRUE.equals()` 处理 Redis 返回的 `Boolean` 类型

## 技术栈

| 组件                | 版本                   |
|---------------------|------------------------|
| JDK                 | 21                     |
| Spring Boot         | 3.4.1                  |
| Spring Cloud        | 2024.0.0 (Moorgate)    |
| Spring Cloud Alibaba| 2023.0.1.0             |
| Nacos               | 2.3.x                  |
| MyBatis-Plus        | 3.5.9 (mybatis-plus-spring-boot3-starter) |
| PostgreSQL          | 16                     |
| Kafka               | 3.x (通过 Spring Kafka)|
| Redis               | 7.x (通过 Lettuce)     |
| JWT (jjwt)          | 0.12.6                 |
| Hutool              | 5.8.34                 |
| Spring Cloud Gateway| (随 Spring Cloud)      |
| OpenFeign           | (随 Spring Cloud)      |
| Resilience4j        | (随 Spring Cloud)      |

## 项目结构

```
MySpringCloudDemo/
├── pom.xml                              # 父 POM（聚合 + 版本管理）
├── common-module/                       # 公共模块
│   └── src/main/java/com/example/common/
│       ├── exception/
│       │   ├── BusinessException.java
│       │   ├── ResourceNotFoundException.java
│       │   └── GlobalExceptionHandler.java
│       ├── dto/
│       │   └── ApiResponse.java
│       └── auth/
│           └── JwtUtil.java
├── gateway-service/                     # API 网关（:8080）
│   └── src/main/java/com/example/gateway/
│       ├── GatewayApplication.java
│       └── filter/JwtAuthFilter.java
├── user-service/                        # 用户服务（:8081）
│   └── src/main/java/com/example/user/
│       ├── controller/AuthController.java
│       ├── controller/UserController.java
│       ├── service/UserService.java
│       ├── entity/User.java
│       └── repository/UserMapper.java
└── content-service/                     # 内容服务（:8082）
    └── src/main/java/com/example/content/
        ├── controller/
        │   ├── ContentController.java
        │   ├── CommentController.java
        │   └── NotificationController.java
        ├── service/
        │   ├── ContentService.java
        │   ├── CommentService.java
        │   └── NotificationService.java
        ├── entity/
        │   ├── Content.java
        │   ├── Comment.java
        │   └── Notification.java
        ├── repository/
        │   ├── ContentMapper.java
        │   ├── CommentMapper.java
        │   └── NotificationMapper.java
        ├── dto/
        │   ├── ContentDTO.java
        │   ├── UserDTO.java
        │   └── CommentDTO.java
        ├── feign/
        │   ├── UserFeignClient.java
        │   └── UserFeignFallbackFactory.java
        ├── event/
        │   ├── CommentEventProducer.java
        │   └── NotificationEventConsumer.java
        └── config/
            └── KafkaTopicConfig.java
```

## 构建与运行

```bash
# 构建全部模块（跳过测试）
mvn clean package -DskipTests

# 编译全部模块
mvn clean compile

# 运行指定服务
mvn spring-boot:run -pl user-service
mvn spring-boot:run -pl content-service
mvn spring-boot:run -pl gateway-service

# 安装公共模块到本地仓库
mvn install -pl common-module -DskipTests
```

## API 接口

### 认证（user-service，通过 Gateway :8080）
- `POST   /api/auth/register`       - 用户注册
- `POST   /api/auth/login`          - 用户登录（返回 JWT Token）

### 用户管理（user-service）
- `GET    /api/users`               - 查询所有用户
- `GET    /api/users/{id}`          - 根据 ID 查询用户
- `PUT    /api/users/{id}`          - 更新用户
- `DELETE /api/users/{id}`          - 删除用户

### 帖子管理（content-service）
- `POST   /api/content`             - 发布帖子
- `GET    /api/content`             - 查询帖子列表
- `GET    /api/content/{id}`        - 查询单篇帖子
- `GET    /api/content/user/{userId}` - 查询某用户的帖子
- `PUT    /api/content/{id}`        - 更新帖子
- `DELETE /api/content/{id}`        - 删除帖子

### 评论管理（content-service）
- `POST   /api/comments`                    - 新增评论
- `GET    /api/comments/content/{contentId}` - 查询某帖子的评论
- `DELETE /api/comments/{id}`               - 删除评论

### 通知管理（content-service）
- `GET    /api/notifications/user/{userId}`  - 查询用户通知
- `PUT    /api/notifications/{id}/read`      - 标记已读

### 异常响应格式

```json
{
  "code": 404,
  "message": "User not found with id: 1"
}
```

| HTTP 状态码 | 场景 |
|-------------|------|
| 400 Bad Request | 业务异常（BusinessException） |
| 401 Unauthorized | Token 缺失或无效 |
| 404 Not Found | 资源未找到（ResourceNotFoundException） |
| 500 Internal Server Error | 未知异常 |

## 数据库

所有服务共用 PostgreSQL 数据库 `appdb`（`localhost:5432`），初始化脚本：`docs/superpowers/plans/init-database.sql`

| 表名 | 所属服务 | 说明 |
|------|----------|------|
| users | user-service | 用户信息 |
| contents | content-service | 帖子/内容 |
| comments | content-service | 评论 |
| notifications | content-service | 通知 |

## 服务间通信

| 调用方 | 被调用方 | 方式 | 场景 |
|--------|----------|------|------|
| content-service | user-service | OpenFeign | 创建帖子时验证用户 |
| content-service（评论） | content-service（帖子） | 内部调用 | 创建评论时验证帖子 |
| content-service（评论） | content-service（通知） | Kafka (`comment-events`) | 评论事件 |

## 测试策略

本项目采用**直接连接本地服务**的测试方式：
- 所有测试使用 `@SpringBootTest` 直接连接本地运行的服务
- 测试前需要手动启动 PostgreSQL、Kafka、Redis、Nacos

## 配置文件

- `application.yml` — 各服务独立配置（端口、数据源）
- `bootstrap.yml` — Nacos 服务器地址及配置中心设置

## 本地开发环境要求

- PostgreSQL，`localhost:5432`，数据库 `appdb`
- Kafka，`localhost:9092`
- Redis，`localhost:6379`
- Nacos，`localhost:8848`（默认账号密码：nacos/nacos）

## 开发进度

- [x] Task 1: 搭建 Maven 多模块骨架（父 POM + 子模块）
- [x] Task 2: 实现 common-module（异常处理 + JWT 工具 + DTO + Hutool）
- [x] Task 3: 迁移 user-service（注册登录 + 用户 CRUD）
- [x] Task 4: 实现 content-service（帖子管理 + OpenFeign）
- [x] Task 5: 实现 gateway-service（路由 + JWT 鉴权）
- [x] Task 6: 合并评论和通知到 content-service（评论 CRUD + Kafka 事件 + 通知）
- [ ] 前端：Vue 3 + Element Plus
