-- =============================================================================
-- 若依书铺 · 业务表结构 v1.0.0
-- 适用框架：RuoYi-Vue v3.9.2（Spring Boot + MyBatis）
-- 生成方式：字段逐个来自 Mapper XML 的 resultMap / insert 语句，与 Java 代码严格一致
-- 执行前置：sql/ry_20260417.sql（框架表 + 系统菜单）、sql/quartz.sql（定时任务表）
--
-- ⚠️ 本脚本会 DROP 并重建业务表，仅用于开发环境重建；生产环境请改用增量脚本。
--
-- 说明：本脚本只建 idle_* 系列业务表（校园教材闲置交易，唯一主线）与预留的钱包两张表。
--   早期版本的 book/course/cart/orders/user 一套模型已于 v1.4.0 连同代码一起移除，
--   老库升级到本版请执行 sql/v1.4.0_drop_legacy_and_index.sql。
-- =============================================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =============================================================================
-- 一、idle_* 系列（当前主线：校园教材闲置交易）
-- =============================================================================

-- ---------------------------------------------------------------- 闲置商品分类
DROP TABLE IF EXISTS `idle_category`;
CREATE TABLE `idle_category` (
  `category_id`   BIGINT(20)   NOT NULL AUTO_INCREMENT           COMMENT '分类ID',
  `parent_id`     BIGINT(20)   DEFAULT 0                         COMMENT '父分类ID，0 为顶级',
  `category_name` VARCHAR(50)  NOT NULL                          COMMENT '分类名称',
  `icon`          VARCHAR(255) DEFAULT ''                        COMMENT '分类图标',
  `status`        VARCHAR(2)   DEFAULT '0'                       COMMENT '状态（0正常 1停用）',
  `remark`        VARCHAR(500) DEFAULT NULL                      COMMENT '备注',
  `create_by`     VARCHAR(64)  DEFAULT ''                        COMMENT '创建者',
  `create_time`   DATETIME     DEFAULT NULL                      COMMENT '创建时间',
  `update_by`     VARCHAR(64)  DEFAULT ''                        COMMENT '更新者',
  `update_time`   DATETIME     DEFAULT NULL                      COMMENT '更新时间',
  PRIMARY KEY (`category_id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='闲置商品分类表';

INSERT INTO `idle_category` (`category_id`, `parent_id`, `category_name`, `icon`, `status`, `create_by`, `create_time`) VALUES
(1, 0, '公共课教材', 'el-icon-reading',   '0', 'admin', NOW()),
(2, 0, '专业课教材', 'el-icon-notebook-2', '0', 'admin', NOW()),
(3, 0, '考研资料',   'el-icon-edit-outline', '0', 'admin', NOW()),
(4, 0, '考证资料',   'el-icon-medal',     '0', 'admin', NOW()),
(5, 0, '课外读物',   'el-icon-collection', '0', 'admin', NOW()),
(6, 0, '其他',       'el-icon-more',      '0', 'admin', NOW());

-- ---------------------------------------------------------------- 闲置商品
-- category_name / publisher_name / audit_user_name 由 SQL 联表查出，不是本表的列
DROP TABLE IF EXISTS `idle_goods`;
CREATE TABLE `idle_goods` (
  `goods_id`       BIGINT(20)    NOT NULL AUTO_INCREMENT          COMMENT '商品ID',
  `category_id`    BIGINT(20)    DEFAULT NULL                     COMMENT '分类ID',
  `goods_name`     VARCHAR(200)  DEFAULT ''                       COMMENT '商品名称（书名）',
  `cover_image`    VARCHAR(500)  DEFAULT ''                       COMMENT '封面图片',
  `price`          DECIMAL(10,2) DEFAULT 0.00                     COMMENT '售价',
  `original_price` DECIMAL(10,2) DEFAULT 0.00                     COMMENT '原价',
  `quality_level`  VARCHAR(20)   DEFAULT ''                       COMMENT '成色（全新/九成新/八成新/七成新）',
  `course_name`    VARCHAR(100)  DEFAULT ''                       COMMENT '关联课程名称',
  `isbn`           VARCHAR(20)   DEFAULT ''                       COMMENT 'ISBN 编号',
  `description`    TEXT                                           COMMENT '商品描述',
  `publisher_id`   BIGINT(20)    DEFAULT NULL                     COMMENT '发布者ID',
  `contact_phone`  VARCHAR(20)   DEFAULT ''                       COMMENT '联系电话',
  `trade_address`  VARCHAR(255)  DEFAULT ''                       COMMENT '交易地点',
  `view_count`     BIGINT(20)    DEFAULT 0                        COMMENT '浏览次数',
  `collect_count`  BIGINT(20)    DEFAULT 0                        COMMENT '收藏次数',
  `audit_status`   VARCHAR(2)    DEFAULT '0'                      COMMENT '审核状态（0待审核 1通过 2驳回）',
  `goods_status`   VARCHAR(2)    DEFAULT '0'                      COMMENT '商品状态（0待审核 1上架 2已预订 3已售出 4已下架）',
  `audit_user`     VARCHAR(64)   DEFAULT ''                       COMMENT '审核人ID（对应 sys_user.user_id）',
  `audit_time`     DATETIME      DEFAULT NULL                     COMMENT '审核时间',
  `audit_remark`   VARCHAR(500)  DEFAULT ''                       COMMENT '审核意见',
  `remark`         VARCHAR(500)  DEFAULT NULL                     COMMENT '备注',
  `create_by`      VARCHAR(64)   DEFAULT ''                       COMMENT '创建者',
  `create_time`    DATETIME      DEFAULT NULL                     COMMENT '创建时间',
  `update_by`      VARCHAR(64)   DEFAULT ''                       COMMENT '更新者',
  `update_time`    DATETIME      DEFAULT NULL                     COMMENT '更新时间',
  PRIMARY KEY (`goods_id`),
  KEY `idx_category`   (`category_id`),
  KEY `idx_publisher`  (`publisher_id`),
  -- C 端商品列表固定形态：WHERE audit_status=? AND goods_status=? ORDER BY create_time DESC
  -- 两个等值列在前、排序列在后，一次索引扫描即可过滤 + 排序（见 v1.4.0 对老库的同步调整）
  KEY `idx_audit_status_time` (`audit_status`, `goods_status`, `create_time`),
  KEY `idx_status`     (`goods_status`),
  KEY `idx_create_time`(`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='闲置商品表';

-- ---------------------------------------------------------------- 闲置订单
DROP TABLE IF EXISTS `idle_order`;
CREATE TABLE `idle_order` (
  `order_id`      BIGINT(20)    NOT NULL AUTO_INCREMENT           COMMENT '订单ID',
  `order_no`      VARCHAR(64)   NOT NULL                          COMMENT '订单编号',
  `goods_id`      BIGINT(20)    DEFAULT NULL                      COMMENT '商品ID',
  `seller_id`     BIGINT(20)    DEFAULT NULL                      COMMENT '卖家ID',
  `buyer_id`      BIGINT(20)    DEFAULT NULL                      COMMENT '买家ID',
  `order_price`   DECIMAL(10,2) DEFAULT 0.00                      COMMENT '成交价格',
  `order_status`  VARCHAR(2)    DEFAULT '0'                       COMMENT '订单状态（0待确认 1交易中 2已完成 3已取消）',
  `finish_time`   DATETIME      DEFAULT NULL                      COMMENT '完成时间',
  `cancel_reason` VARCHAR(500)  DEFAULT ''                        COMMENT '取消原因',
  `remark`        VARCHAR(500)  DEFAULT NULL                      COMMENT '备注',
  `create_by`     VARCHAR(64)   DEFAULT ''                        COMMENT '创建者',
  `create_time`   DATETIME      DEFAULT NULL                      COMMENT '创建时间',
  `update_by`     VARCHAR(64)   DEFAULT ''                        COMMENT '更新者',
  `update_time`   DATETIME      DEFAULT NULL                      COMMENT '更新时间',
  PRIMARY KEY (`order_id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_buyer_status`  (`buyer_id`, `order_status`),
  KEY `idx_seller_status` (`seller_id`, `order_status`),
  KEY `idx_goods`         (`goods_id`),
  KEY `idx_create_time`   (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='闲置交易订单表';

-- ---------------------------------------------------------------- 商品收藏
DROP TABLE IF EXISTS `idle_favorite`;
CREATE TABLE `idle_favorite` (
  `favorite_id` BIGINT(20) NOT NULL AUTO_INCREMENT                COMMENT '收藏ID',
  `user_id`     BIGINT(20) NOT NULL                               COMMENT '用户ID',
  `goods_id`    BIGINT(20) NOT NULL                               COMMENT '商品ID',
  `create_time` DATETIME   DEFAULT CURRENT_TIMESTAMP              COMMENT '收藏时间',
  PRIMARY KEY (`favorite_id`),
  UNIQUE KEY `uk_user_goods` (`user_id`, `goods_id`),
  KEY `idx_goods` (`goods_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='商品收藏表';

-- ---------------------------------------------------------------- 钱包 / 收支流水
DROP TABLE IF EXISTS `wallet`;
CREATE TABLE `wallet` (
  `id`            BIGINT(20)    NOT NULL AUTO_INCREMENT           COMMENT '钱包ID',
  `user_id`       BIGINT(20)    NOT NULL                          COMMENT '用户ID',
  `balance`       DECIMAL(12,2) DEFAULT 0.00                      COMMENT '可用余额',
  `freeze_amount` DECIMAL(12,2) DEFAULT 0.00                      COMMENT '冻结金额',
  `create_time`   DATETIME      DEFAULT CURRENT_TIMESTAMP         COMMENT '创建时间',
  `update_time`   DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='钱包表';

DROP TABLE IF EXISTS `wallet_record`;
CREATE TABLE `wallet_record` (
  `id`          BIGINT(20)    NOT NULL AUTO_INCREMENT             COMMENT '记录ID',
  `user_id`     BIGINT(20)    NOT NULL                            COMMENT '用户ID',
  `type`        VARCHAR(20)   DEFAULT ''                          COMMENT '类型 income-收入 expense-支出',
  `type_text`   VARCHAR(50)   DEFAULT ''                          COMMENT '类型描述',
  `amount`      DECIMAL(10,2) NOT NULL                            COMMENT '金额',
  `balance`     DECIMAL(12,2) NOT NULL                            COMMENT '变动后余额',
  `remark`      VARCHAR(500)  DEFAULT ''                          COMMENT '备注',
  `create_time` DATETIME      DEFAULT CURRENT_TIMESTAMP           COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_time` (`user_id`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='钱包收支记录表';

-- =============================================================================
-- 二、框架参数调整（不改框架表结构，只改业务需要的开关）
-- =============================================================================
-- 开放自助注册，C 端用户才能注册（框架默认 false）
UPDATE `sys_config` SET `config_value` = 'true' WHERE `config_key` = 'sys.account.registerUser';

SET FOREIGN_KEY_CHECKS = 1;
