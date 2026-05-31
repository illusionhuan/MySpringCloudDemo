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
| PostgreSQL          | 16                     |
| Kafka               | 3.x (通过 Spring Kafka)|
| Redis               | 7.x (通过 Lettuce)     |

## 项目结构

```
src/main/java/com/example/demo/
├── DemoApplication.java           # 启动类，@EnableDiscoveryClient
├── config/
│   ├── RedisConfig.java           # RedisTemplate JSON 序列化配置
│   └── KafkaConfig.java           # Kafka Topic 定义
├── entity/
│   └── User.java                  # JPA 实体
├── exception/
│   ├── BusinessException.java     # 业务异常基类
│   ├── ResourceNotFoundException.java # 资源未找到异常
│   └── GlobalExceptionHandler.java    # 全局异常处理器
├── repository/
│   └── UserRepository.java        # Spring Data JPA 仓库
├── service/
│   ├── UserService.java           # 用户 CRUD (Postgres)
│   ├── RedisService.java          # Redis 缓存操作
│   └── KafkaProducerService.java  # Kafka 消息发送
├── consumer/
│   └── KafkaConsumerService.java  # Kafka @KafkaListener 消费者
└── controller/
    ├── UserController.java        # /api/users 用户接口
    ├── RedisController.java       # /api/redis 缓存接口
    └── KafkaController.java       # /api/kafka 消息接口
```

## 构建与运行

```bash
# 构建（跳过测试）
mvn clean package -DskipTests

# 运行（需要本地启动 Postgres、Kafka、Redis、Nacos）
mvn spring-boot:run

# 运行测试（需要本地启动所有服务）
mvn test

# 运行指定测试类
mvn test -Dtest=PostgresRepositoryTest

# 跳过 Nacos 相关测试
mvn test -Dtest='!NacosConfigTest'
```

## API 接口

### PostgreSQL - 用户增删改查
- `POST   /api/users`       - 创建用户
- `GET    /api/users`        - 查询所有用户
- `GET    /api/users/{id}`   - 根据 ID 查询用户
- `PUT    /api/users/{id}`   - 更新用户
- `DELETE /api/users/{id}`   - 删除用户

### Redis 缓存
- `POST   /api/redis/set?key=&value=`                    - 设置键值
- `POST   /api/redis/set-with-ttl?key=&value=&timeout=`  - 设置带过期时间的键值（秒）
- `GET    /api/redis/get?key=`                            - 获取值
- `DELETE /api/redis/delete?key=`                         - 删除键
- `GET    /api/redis/exists?key=`                         - 检查键是否存在

### Kafka 消息
- `POST   /api/kafka/send?key=&message=`  - 向 demo-topic 发送消息

### 异常响应格式

当请求发生错误时，API 返回统一格式：

```json
{
  "code": "RESOURCE_NOT_FOUND",
  "message": "User not found with id: 1"
}
```

| HTTP 状态码 | 场景 |
|-------------|------|
| 400 Bad Request | 业务异常（BusinessException） |
| 404 Not Found | 资源未找到（ResourceNotFoundException） |
| 500 Internal Server Error | 未知异常 |

## 测试类说明

| 测试类                      | 测试内容                        | 依赖环境           |
|-----------------------------|--------------------------------|--------------------|
| `NacosConfigTest`          | 配置加载、bootstrap.yml 属性     | 需要运行 Nacos      |
| `PostgresRepositoryTest`   | JPA 增删改查                    | 需要运行 PostgreSQL |
| `RedisServiceTest`         | Redis 操作                      | 需要运行 Redis      |
| `KafkaProducerConsumerTest`| Kafka 生产消费                  | 需要运行 Kafka      |

## 测试策略

本项目为小型示例项目，采用**直接连接本地服务**的测试方式：

- 所有测试使用 `@SpringBootTest` 直接连接本地运行的服务
- 测试配置直接复用 `application.yml`，无需额外测试配置文件
- 测试前需要手动启动 Docker 中的 PostgreSQL、Kafka、Redis、Nacos

**为什么不用 Testcontainers 或 Embedded-Redis：**
- 项目规模小，不需要复杂的测试环境隔离
- 避免 Docker 兼容性问题（如 Docker Desktop 版本不兼容）
- 测试速度更快，配置更简单
- 本地开发时已运行所有服务，直接复用即可

## 配置文件

- `application.yml` — 主配置文件（数据库、Kafka、Redis、Actuator）
- `bootstrap.yml`   — Nacos 服务器地址及配置中心设置

## 本地开发环境要求

本地开发需要启动以下服务：
- PostgreSQL，运行在 `localhost:5432`，数据库名 `demo_db`
- Kafka，运行在 `localhost:9092`
- Redis，运行在 `localhost:6379`
- Nacos，运行在 `localhost:8848`（默认账号密码：nacos/nacos）
