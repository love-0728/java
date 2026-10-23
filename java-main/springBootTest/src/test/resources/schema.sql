-- 测试用 H2 建表脚本（与 kitty_db.sql 结构一致，去掉 MySQL 专属语法）
CREATE TABLE IF NOT EXISTS `user` (
    `id`          BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username`    VARCHAR(32) NOT NULL,
    `password`    VARCHAR(64) NOT NULL,
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX IF NOT EXISTS `uk_username` ON `user` (`username`);

CREATE TABLE IF NOT EXISTS `greet_log` (
    `id`          BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username`    VARCHAR(32) NOT NULL,
    `member_name` VARCHAR(64) NOT NULL,
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS `idx_username` ON `greet_log` (`username`);
