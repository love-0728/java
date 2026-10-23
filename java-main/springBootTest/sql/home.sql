-- ============================================================
-- Hello Kitty 首页 数据库脚本（展示 + 分级分类搜索）
-- 数据库：kitty_db
-- 使用方式：mysql -uAPT -p123456 < home.sql
-- 幂等：每次重置后重新执行即可（CREATE TABLE IF NOT EXISTS + 重新种子）
-- ============================================================

USE kitty_db;

-- 首页分类表（分级分类：parent_id 指向自身 id 形成层级，0 表示一级）
CREATE TABLE IF NOT EXISTS `home_category` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `parent_id`   BIGINT       NOT NULL DEFAULT 0 COMMENT '父分类 id；0 表示一级分类',
    `name`        VARCHAR(64)  NOT NULL COMMENT '分类名称',
    `sort`        INT          NOT NULL DEFAULT 0 COMMENT '同级排序',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_parent` (`parent_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT ='首页分类（支持分级）';

-- 首页内容/成员表（首页展示与搜索的数据源）
CREATE TABLE IF NOT EXISTS `home_item` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `category_id` BIGINT       NOT NULL DEFAULT 0 COMMENT '所属分类 id（对应 home_category.id）',
    `title`       VARCHAR(128) NOT NULL COMMENT '标题 / 成员名',
    `description` VARCHAR(255) DEFAULT '' COMMENT '简介',
    `image_url`   VARCHAR(255) DEFAULT '' COMMENT '图片地址（相对路径，由前端按自身域名解析）',
    `tags`        VARCHAR(255) DEFAULT '' COMMENT '标签，逗号分隔，便于搜索',
    `sort`        INT          NOT NULL DEFAULT 0 COMMENT '排序',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_category` (`category_id`),
    KEY `idx_title` (`title`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT ='首页内容 / 家族成员';

-- ===== 种子数据（每次重置后重新填充，幂等）=====
DELETE FROM `home_item`;
DELETE FROM `home_category`;

INSERT INTO `home_category` (`id`, `parent_id`, `name`, `sort`) VALUES
(1, 0, '家族成员', 1),
(2, 1, '主角',     1),
(3, 1, '好朋友',   2),
(4, 1, '宠物伙伴', 3),
(5, 0, '主题内容', 2),
(6, 5, '相册',     1),
(7, 5, '社区话题', 2);

INSERT INTO `home_item` (`category_id`, `title`, `description`, `image_url`, `tags`, `sort`) VALUES
(2, '棉花糖', '元气满满的小甜心',       'characters/char_01.png', '甜美,元气',   1),
(3, '草莓酱', '最爱做甜点的小厨师',     'characters/char_02.png', '甜点,美食',   2),
(3, '小铃铛', '每天都有用不完的活力',   'characters/char_04.png', '活力,朋友',   3),
(4, '薄荷糖', '清凉又安静的治愈系',     'characters/char_05.png', '清凉,治愈',   4),
(2, '布丁',   '软软糯糯的人气王',       'characters/char_06.png', '软萌,主角',   5),
(3, '蜜桃',   '甜美系的小可爱',         'characters/char_08.png', '甜美,朋友',   6),
(4, '可可',   '暖暖的像一杯热可可',     'characters/char_09.png', '暖心,宠物',   7),
(2, '云朵',   '飘忽忽的治愈担当',       'characters/char_10.png', '治愈,主角',   8),
(3, '樱桃',   '俏皮又古灵精怪',         'characters/char_11.png', '俏皮,朋友',   9),
(4, '蓝莓',   '安安静静的小蓝胖',       'characters/char_12.png', '安静,宠物',  10),
(2, '月亮船', '夜晚陪你聊天的伙伴',     'characters/char_03.png', '安静,主角',  11),
(3, '星之卡', '神秘又闪亮的存在',       'characters/char_07.png', '神秘,朋友',  12);
