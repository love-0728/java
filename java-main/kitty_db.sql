/*
 Navicat Premium Dump SQL

 Source Server         : test
 Source Server Type    : MySQL
 Source Server Version : 80039 (8.0.39)
 Source Host           : localhost:3306
 Source Schema         : kitty_db

 Target Server Type    : MySQL
 Target Server Version : 80039 (8.0.39)
 File Encoding         : 65001

 Date: 16/10/2026 16:25:35
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for greet_log
-- ----------------------------
DROP TABLE IF EXISTS `greet_log`;
CREATE TABLE `greet_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭',
  `username` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '鎵撴嫑鍛肩殑鐢ㄦ埛',
  `member_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '瀹舵棌鎴愬憳鍚嶅瓧',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鎵撴嫑鍛兼椂闂',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '鎵撴嫑鍛艰?褰曡〃' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of greet_log
-- ----------------------------

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭',
  `username` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '鐢ㄦ埛鍚',
  `password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '瀵嗙爜锛堟紨绀洪」鐩?负鏄庢枃锛岀敓浜х幆澧冭?鍔犲瘑瀛樺偍锛',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '娉ㄥ唽鏃堕棿',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '鐢ㄦ埛琛' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'kitty', '123456', '2026-10-16 15:51:56');

SET FOREIGN_KEY_CHECKS = 1;
