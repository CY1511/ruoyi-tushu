-- =============================================================================
-- 若依书铺 · 业务表结构 v1.0.0
-- 适用框架：RuoYi-Vue v3.9.2（Spring Boot + MyBatis）
-- 生成方式：字段逐个来自 Mapper XML 的 resultMap / insert 语句，与 Java 代码严格一致
-- 执行前置：sql/ry_20260417.sql（框架表 + 系统菜单）、sql/quartz.sql（定时任务表）
--
-- ⚠️ 本脚本会 DROP 并重建业务表，仅用于开发环境重建；生产环境请改用增量脚本。
--
-- 说明：项目当前并存两代业务模型，本脚本两代都给全，保证克隆后可直接启动：
--   ① idle_* 系列（校园教材闲置交易，管理端有页面，是当前主线）
--   ② book/course/cart/orders 系列（早期版本，代码仍在，无管理端页面）
-- 后续若要"定稿一套"，建议删掉第 ② 代代码与表，详见 sql/README.md。
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
  KEY `idx_audit`      (`audit_status`),
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
-- 二、book / course / cart / orders 系列（早期版本，代码仍在仓库中）
-- =============================================================================

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT              COMMENT '用户ID',
  `phone`       VARCHAR(20)  NOT NULL                             COMMENT '手机号',
  `password`    VARCHAR(100) NOT NULL                             COMMENT '密码',
  `nickname`    VARCHAR(50)  DEFAULT ''                           COMMENT '昵称',
  `avatar`      VARCHAR(255) DEFAULT ''                           COMMENT '头像',
  `school_name` VARCHAR(100) DEFAULT ''                           COMMENT '学校名称',
  `student_id`  VARCHAR(50)  DEFAULT ''                           COMMENT '学号',
  `department`  VARCHAR(100) DEFAULT ''                           COMMENT '院系',
  `balance`     DECIMAL(12,2) DEFAULT 0.00                        COMMENT '余额',
  `status`      TINYINT      DEFAULT 1                            COMMENT '状态 0-禁用 1-正常',
  `created_at`  DATETIME     DEFAULT CURRENT_TIMESTAMP            COMMENT '创建时间',
  `updated_at`  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
  `id`         BIGINT       NOT NULL AUTO_INCREMENT               COMMENT '分类ID',
  `name`       VARCHAR(50)  NOT NULL                              COMMENT '分类名称',
  `icon`       VARCHAR(255) DEFAULT ''                            COMMENT '分类图标',
  `parent_id`  BIGINT       DEFAULT 0                             COMMENT '父分类ID',
  `sort_order` INT          DEFAULT 0                             COMMENT '排序',
  `status`     TINYINT      DEFAULT 1                             COMMENT '状态 0-禁用 1-正常',
  `create_time` DATETIME    DEFAULT CURRENT_TIMESTAMP             COMMENT '创建时间',
  `update_time` DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类表';

INSERT INTO `category` (`id`, `name`, `icon`, `parent_id`, `sort_order`, `status`) VALUES
(1, '教材', '📚', 0, 1, 1),
(2, '文学小说', '📖', 0, 2, 1),
(3, '教辅资料', '📝', 0, 3, 1),
(4, '计算机', '💻', 1, 1, 1),
(5, '数学', '📐', 1, 2, 1),
(6, '英语', '🔤', 1, 3, 1),
(7, '物理', '⚛️', 1, 4, 1),
(8, '化学', '🧪', 1, 5, 1),
(9, '历史', '📜', 1, 6, 1),
(10, '经济管理', '📊', 1, 7, 1);

-- 注意：condition 是 MySQL 保留字，建表与查询都必须加反引号
DROP TABLE IF EXISTS `book`;
CREATE TABLE `book` (
  `id`                 BIGINT       NOT NULL AUTO_INCREMENT       COMMENT '教材ID',
  `seller_id`          BIGINT       NOT NULL                      COMMENT '卖家ID',
  `seller_name`        VARCHAR(50)  DEFAULT ''                    COMMENT '卖家名称',
  `seller_avatar`      VARCHAR(255) DEFAULT ''                    COMMENT '卖家头像',
  `seller_rating`      DECIMAL(2,1) DEFAULT 5.0                   COMMENT '卖家评分',
  `isbn`               VARCHAR(20)  DEFAULT ''                    COMMENT 'ISBN编号',
  `title`              VARCHAR(200) NOT NULL                      COMMENT '书名',
  `author`             VARCHAR(100) DEFAULT ''                    COMMENT '作者',
  `publisher`          VARCHAR(100) DEFAULT ''                    COMMENT '出版社',
  `edition`            VARCHAR(50)  DEFAULT ''                    COMMENT '版本',
  `publish_year`       INT          DEFAULT 0                     COMMENT '出版年份',
  `original_price`     DECIMAL(10,2) DEFAULT 0.00                 COMMENT '原价',
  `cover_image`        VARCHAR(500) DEFAULT ''                    COMMENT '封面图片',
  `condition`          TINYINT      DEFAULT 1                     COMMENT '成色等级 1-全新 2-九成 3-八成 4-七成',
  `condition_desc`     VARCHAR(500) DEFAULT ''                    COMMENT '成色描述',
  `images`             TEXT                                       COMMENT '实拍图片JSON',
  `price`              DECIMAL(10,2) NOT NULL                     COMMENT '售价',
  `category_id`        INT          DEFAULT 0                     COMMENT '分类ID',
  `category_name`      VARCHAR(50)  DEFAULT ''                    COMMENT '分类名称',
  `course_id`          BIGINT       DEFAULT 0                     COMMENT '关联课程ID',
  `course_name`        VARCHAR(100) DEFAULT ''                    COMMENT '关联课程名称',
  `university`         VARCHAR(100) DEFAULT ''                    COMMENT '院校',
  `stock`              INT          DEFAULT 1                     COMMENT '库存',
  `status`             TINYINT      DEFAULT 1                     COMMENT '状态 0-下架 1-上架',
  `description`        TEXT                                       COMMENT '描述',
  `sales`              INT          DEFAULT 0                     COMMENT '销量',
  `views`              INT          DEFAULT 0                     COMMENT '浏览量',
  `favorites`          INT          DEFAULT 0                     COMMENT '收藏量',
  `price_evaluation`   TINYINT      DEFAULT 2                     COMMENT '价格评估 1-偏低 2-合理 3-偏高',
  `suggested_price_min` DECIMAL(10,2) DEFAULT 0.00                COMMENT '建议售价下限',
  `suggested_price_max` DECIMAL(10,2) DEFAULT 0.00                COMMENT '建议售价上限',
  `is_favorited`       TINYINT      DEFAULT 0                     COMMENT '是否已收藏',
  `create_time`        DATETIME     DEFAULT CURRENT_TIMESTAMP     COMMENT '创建时间',
  `update_time`        DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_seller_id` (`seller_id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_status` (`status`),
  FULLTEXT KEY `ft_title_author` (`title`, `author`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教材表';

DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart` (
  `id`          BIGINT   NOT NULL AUTO_INCREMENT                  COMMENT '购物车ID',
  `user_id`     BIGINT   NOT NULL                                 COMMENT '用户ID',
  `book_id`     BIGINT   NOT NULL                                 COMMENT '教材ID',
  `quantity`    INT      DEFAULT 1                                COMMENT '数量',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP                COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_book` (`user_id`, `book_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
  `id`               BIGINT       NOT NULL AUTO_INCREMENT         COMMENT '订单ID',
  `order_no`         VARCHAR(50)  NOT NULL                        COMMENT '订单编号',
  `buyer_id`         BIGINT       NOT NULL                        COMMENT '买家ID',
  `buyer_name`       VARCHAR(50)  DEFAULT ''                      COMMENT '买家名称',
  `buyer_phone`      VARCHAR(20)  DEFAULT ''                      COMMENT '买家手机号',
  `seller_id`        BIGINT       NOT NULL                        COMMENT '卖家ID',
  `seller_name`      VARCHAR(50)  DEFAULT ''                      COMMENT '卖家名称',
  `seller_avatar`    VARCHAR(255) DEFAULT ''                      COMMENT '卖家头像',
  `total_amount`     DECIMAL(10,2) NOT NULL                       COMMENT '订单总金额',
  `pay_amount`       DECIMAL(10,2) DEFAULT 0.00                   COMMENT '实付金额',
  `delivery_method`  TINYINT      DEFAULT 3                       COMMENT '配送方式 1-校内自取 2-同城配送 3-快递',
  `address`          VARCHAR(500) DEFAULT ''                      COMMENT '收货地址',
  `pickup_point`     VARCHAR(200) DEFAULT ''                      COMMENT '自取点',
  `pay_method`       VARCHAR(20)  DEFAULT ''                      COMMENT '支付方式',
  `status`           TINYINT      DEFAULT 1                       COMMENT '订单状态 1-待付款 2-待发货 3-待自取 4-已发货 5-待收货 6-已完成 7-已关闭 8-退款中 9-已退款',
  `remark`           VARCHAR(500) DEFAULT ''                      COMMENT '备注',
  `logistics_no`     VARCHAR(50)  DEFAULT ''                      COMMENT '物流单号',
  `create_time`      DATETIME     DEFAULT CURRENT_TIMESTAMP       COMMENT '创建时间',
  `pay_time`         DATETIME     DEFAULT NULL                    COMMENT '支付时间',
  `ship_time`        DATETIME     DEFAULT NULL                    COMMENT '发货时间',
  `complete_time`    DATETIME     DEFAULT NULL                    COMMENT '完成时间',
  `close_time`       DATETIME     DEFAULT NULL                    COMMENT '关闭时间',
  `refund_reason`    VARCHAR(500) DEFAULT ''                      COMMENT '退款原因',
  `refund_description` TEXT                                       COMMENT '退款描述',
  `refund_images`    TEXT                                         COMMENT '退款凭证',
  `refund_time`      DATETIME     DEFAULT NULL                    COMMENT '退款申请时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_buyer_id` (`buyer_id`),
  KEY `idx_seller_id` (`seller_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT              COMMENT '订单项ID',
  `order_id`    BIGINT       NOT NULL                             COMMENT '订单ID',
  `book_id`     BIGINT       NOT NULL                             COMMENT '教材ID',
  `title`       VARCHAR(200) NOT NULL                             COMMENT '书名',
  `cover_image` VARCHAR(500) DEFAULT ''                           COMMENT '封面图片',
  `price`       DECIMAL(10,2) NOT NULL                            COMMENT '单价',
  `quantity`    INT          DEFAULT 1                            COMMENT '数量',
  `condition`   TINYINT      DEFAULT 1                            COMMENT '成色',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单项表';

DROP TABLE IF EXISTS `address`;
CREATE TABLE `address` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT              COMMENT '地址ID',
  `user_id`     BIGINT       NOT NULL                             COMMENT '用户ID',
  `name`        VARCHAR(50)  NOT NULL                             COMMENT '收货人',
  `phone`       VARCHAR(20)  NOT NULL                             COMMENT '手机号',
  `province`    VARCHAR(50)  DEFAULT ''                           COMMENT '省份',
  `city`        VARCHAR(50)  DEFAULT ''                           COMMENT '城市',
  `district`    VARCHAR(50)  DEFAULT ''                           COMMENT '区县',
  `detail`      VARCHAR(500) NOT NULL                             COMMENT '详细地址',
  `is_default`  TINYINT      DEFAULT 0                            COMMENT '是否默认 0-否 1-是',
  `status`      TINYINT      DEFAULT 1                            COMMENT '状态 0-删除 1-正常',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP            COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收货地址表';

DROP TABLE IF EXISTS `favorites`;
CREATE TABLE `favorites` (
  `id`          BIGINT   NOT NULL AUTO_INCREMENT                  COMMENT '收藏ID',
  `user_id`     BIGINT   NOT NULL                                 COMMENT '用户ID',
  `book_id`     BIGINT   NOT NULL                                 COMMENT '教材ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP                COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_book` (`user_id`, `book_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教材收藏表';

DROP TABLE IF EXISTS `review`;
CREATE TABLE `review` (
  `id`          BIGINT   NOT NULL AUTO_INCREMENT                  COMMENT '评价ID',
  `order_id`    BIGINT   NOT NULL                                 COMMENT '订单ID',
  `user_id`     BIGINT   NOT NULL                                 COMMENT '评价用户ID',
  `book_id`     BIGINT   NOT NULL                                 COMMENT '教材ID',
  `rating`      TINYINT  DEFAULT 5                                COMMENT '评分 1-5',
  `content`     TEXT                                              COMMENT '评价内容',
  `images`      TEXT                                              COMMENT '评价图片',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP                COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_book_id` (`book_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价表';

DROP TABLE IF EXISTS `message`;
CREATE TABLE `message` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT              COMMENT '消息ID',
  `user_id`     BIGINT       NOT NULL                             COMMENT '用户ID',
  `type`        VARCHAR(20)  DEFAULT ''                           COMMENT '消息类型',
  `title`       VARCHAR(200) DEFAULT ''                           COMMENT '消息标题',
  `content`     TEXT                                              COMMENT '消息内容',
  `is_read`     TINYINT      DEFAULT 0                            COMMENT '是否已读 0-否 1-是',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP            COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_is_read` (`is_read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息表';

-- 修复：CourseMapper.xml 的 insert 使用 (name, code, department, university, credit)，
--       原 schema.sql 缺 code / department 两列，此处补齐，否则插入报 Unknown column
DROP TABLE IF EXISTS `course`;
CREATE TABLE `course` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT              COMMENT '课程ID',
  `name`        VARCHAR(200) NOT NULL                             COMMENT '课程名称',
  `code`        VARCHAR(50)  DEFAULT ''                           COMMENT '课程代码',
  `department`  VARCHAR(100) DEFAULT ''                           COMMENT '开课院系',
  `university`  VARCHAR(100) NOT NULL                             COMMENT '院校',
  `credit`      DECIMAL(3,1) DEFAULT 0.0                          COMMENT '学分',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP            COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_university` (`university`),
  KEY `idx_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

DROP TABLE IF EXISTS `course_book`;
CREATE TABLE `course_book` (
  `id`        BIGINT NOT NULL AUTO_INCREMENT                      COMMENT '关联ID',
  `course_id` BIGINT NOT NULL                                     COMMENT '课程ID',
  `book_id`   BIGINT NOT NULL                                     COMMENT '教材ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_course_book` (`course_id`, `book_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程-教材关联表';

-- 修复：TradeMapper.xml 使用 trade 表，但原仓库任何脚本都没有它的建表语句
DROP TABLE IF EXISTS `trade`;
CREATE TABLE `trade` (
  `id`          BIGINT        NOT NULL AUTO_INCREMENT             COMMENT '流水ID',
  `user_id`     BIGINT        NOT NULL                            COMMENT '用户ID',
  `type`        VARCHAR(20)   DEFAULT ''                          COMMENT '类型',
  `amount`      DECIMAL(10,2) NOT NULL                            COMMENT '金额',
  `balance`     DECIMAL(12,2) NOT NULL                            COMMENT '变动后余额',
  `order_no`    VARCHAR(64)   DEFAULT ''                          COMMENT '关联订单号',
  `description` VARCHAR(500)  DEFAULT ''                          COMMENT '描述',
  `create_time` DATETIME      DEFAULT CURRENT_TIMESTAMP           COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_time` (`user_id`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交易流水表';

-- =============================================================================
-- 三、框架参数调整（不改框架表结构，只改业务需要的开关）
-- =============================================================================
-- 开放自助注册，C 端用户才能注册（框架默认 false）
UPDATE `sys_config` SET `config_value` = 'true' WHERE `config_key` = 'sys.account.registerUser';

SET FOREIGN_KEY_CHECKS = 1;
