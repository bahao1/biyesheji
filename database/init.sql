-- 创建数据库
CREATE DATABASE IF NOT EXISTS campus_wall DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE campus_wall;

-- 用户表会由 JPA 自动创建，这里提供手动创建的 SQL 作为参考
-- CREATE TABLE IF NOT EXISTS `user` (
--   `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
--   `username` VARCHAR(50) NOT NULL COMMENT '用户名',
--   `password` VARCHAR(255) NOT NULL COMMENT '密码',
--   `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
--   `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
--   `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
--   `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
--   `role` VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色：USER/CLUB_ADMIN/SUPER_ADMIN',
--   `club_id` BIGINT DEFAULT NULL COMMENT '社团ID（社团管理员）',
--   `enabled` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
--   `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
--   `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
--   PRIMARY KEY (`id`),
--   UNIQUE KEY `uk_username` (`username`),
--   KEY `idx_email` (`email`),
--   KEY `idx_phone` (`phone`)
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 插入测试数据（密码都是 123456，已使用 BCrypt 加密）
INSERT INTO `user` (`username`, `password`, `nickname`, `phone`, `email`, `role`, `enabled`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '超级管理员', '13800138000', 'admin@campus.com', 'SUPER_ADMIN', 1),
('club_admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '社团管理员', '13800138001', 'club@campus.com', 'CLUB_ADMIN', 1),
('user001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '普通用户1', '13800138002', 'user001@campus.com', 'USER', 1);
