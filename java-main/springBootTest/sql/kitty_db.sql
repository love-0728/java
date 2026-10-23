-- ============================================================
-- Hello Kitty 登录/注册演示 数据库脚本
-- 数据库：kitty_db
-- 使用方式：mysql -uAPT -p123456 < kitty_db.sql
--          或在 Navicat / IDEA 数据库工具中直接执行本文件
-- ============================================================

CREATE DATABASE IF NOT EXISTS kitty_db
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci;

USE kitty_db;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `username`    VARCHAR(32)  NOT NULL COMMENT '用户名',
    `password`    VARCHAR(64)  NOT NULL COMMENT '密码（演示项目为明文，生产环境请加密存储）',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT ='用户表';

-- 测试账号（可选）
INSERT INTO `user` (`username`, `password`)
SELECT 'kitty', '123456'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `user` WHERE `username` = 'kitty');

-- 打招呼记录表
CREATE TABLE IF NOT EXISTS `greet_log` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `username`    VARCHAR(32)  NOT NULL COMMENT '打招呼的用户',
    `member_name` VARCHAR(64)  NOT NULL COMMENT '家族成员名字',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '打招呼时间',
    PRIMARY KEY (`id`),
    KEY `idx_username` (`username`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT ='打招呼记录表';
