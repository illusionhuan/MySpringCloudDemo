-- ============================================
-- 微服务数据库初始化脚本
-- 单库：appdb @ PostgreSQL 16 (localhost:5432)
-- ============================================

-- ============================================================
-- 1. 创建数据库（以 postgres 超级用户执行）
-- ============================================================

CREATE DATABASE appdb;

-- ============================================================
-- 2. 切换到 appdb 后执行以下建表语句
-- ============================================================

\c appdb;

-- ============================================================
-- 用户表（user-service）
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(50),
    email VARCHAR(100),
    phone VARCHAR(20)
);

COMMENT ON TABLE users IS '用户表，存储注册用户信息，由 user-service 管理';
COMMENT ON COLUMN users.id IS '用户ID，自增主键';
COMMENT ON COLUMN users.username IS '登录用户名，全局唯一';
COMMENT ON COLUMN users.password IS '登录密码，BCrypt 加密存储';
COMMENT ON COLUMN users.name IS '用户显示名称';
COMMENT ON COLUMN users.email IS '邮箱地址';
COMMENT ON COLUMN users.phone IS '手机号';

-- ============================================================
-- 帖子/内容表（content-service）
-- ============================================================
CREATE TABLE IF NOT EXISTS contents (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    body TEXT,
    status VARCHAR(20) DEFAULT 'PUBLISHED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE contents IS '帖子/内容表，存储用户发布的帖子，由 content-service 管理';
COMMENT ON COLUMN contents.id IS '帖子ID，自增主键';
COMMENT ON COLUMN contents.user_id IS '作者ID，关联 users 表';
COMMENT ON COLUMN contents.title IS '帖子标题';
COMMENT ON COLUMN contents.body IS '帖子正文内容';
COMMENT ON COLUMN contents.status IS '帖子状态：DRAFT-草稿 / PUBLISHED-已发布 / DELETED-已删除';
COMMENT ON COLUMN contents.created_at IS '创建时间';
COMMENT ON COLUMN contents.updated_at IS '最后更新时间';

-- ============================================================
-- 评论表（comment-service）
-- ============================================================
CREATE TABLE IF NOT EXISTS comments (
    id BIGSERIAL PRIMARY KEY,
    content_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    body TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE comments IS '评论表，存储帖子下的评论，由 comment-service 管理';
COMMENT ON COLUMN comments.id IS '评论ID，自增主键';
COMMENT ON COLUMN comments.content_id IS '关联帖子ID，关联 contents 表';
COMMENT ON COLUMN comments.user_id IS '评论者ID，关联 users 表';
COMMENT ON COLUMN comments.body IS '评论内容';
COMMENT ON COLUMN comments.created_at IS '评论时间';

-- ============================================================
-- 通知表（notification-service）
-- ============================================================
CREATE TABLE IF NOT EXISTS notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    message TEXT NOT NULL,
    read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE notifications IS '通知表，存储系统通知消息，由 notification-service 管理';
COMMENT ON COLUMN notifications.id IS '通知ID，自增主键';
COMMENT ON COLUMN notifications.user_id IS '接收通知的用户ID，关联 users 表';
COMMENT ON COLUMN notifications.type IS '通知类型：CONTENT_PUBLISHED-帖子发布 / COMMENT_ADDED-新增评论';
COMMENT ON COLUMN notifications.message IS '通知内容描述';
COMMENT ON COLUMN notifications.read IS '是否已读：false-未读 / true-已读';
COMMENT ON COLUMN notifications.created_at IS '通知创建时间';
