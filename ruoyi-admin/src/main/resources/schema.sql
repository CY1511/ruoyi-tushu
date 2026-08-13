CREATE TABLE IF NOT EXISTS `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `phone` VARCHAR(20) NOT NULL COMMENT '手机号',
  `password` VARCHAR(100) NOT NULL COMMENT '密码',
  `nickname` VARCHAR(50) DEFAULT '' COMMENT '昵称',
  `avatar` VARCHAR(255) DEFAULT '' COMMENT '头像',
  `school_name` VARCHAR(100) DEFAULT '' COMMENT '学校名称',
  `student_id` VARCHAR(50) DEFAULT '' COMMENT '学号',
  `department` VARCHAR(100) DEFAULT '' COMMENT '院系',
  `balance` DECIMAL(12,2) DEFAULT 0.00 COMMENT '余额',
  `status` TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE IF NOT EXISTS `category` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `icon` VARCHAR(255) DEFAULT '' COMMENT '分类图标',
  `parent_id` BIGINT DEFAULT 0 COMMENT '父分类ID',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `status` TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_parent_id` (`parent_id`),
  INDEX `idx_status` (`status`)
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

CREATE TABLE IF NOT EXISTS `book` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '教材ID',
  `seller_id` BIGINT NOT NULL COMMENT '卖家ID',
  `seller_name` VARCHAR(50) DEFAULT '' COMMENT '卖家名称',
  `seller_avatar` VARCHAR(255) DEFAULT '' COMMENT '卖家头像',
  `seller_rating` DECIMAL(2,1) DEFAULT 5.0 COMMENT '卖家评分',
  `isbn` VARCHAR(20) DEFAULT '' COMMENT 'ISBN编号',
  `title` VARCHAR(200) NOT NULL COMMENT '书名',
  `author` VARCHAR(100) DEFAULT '' COMMENT '作者',
  `publisher` VARCHAR(100) DEFAULT '' COMMENT '出版社',
  `edition` VARCHAR(50) DEFAULT '' COMMENT '版本',
  `publish_year` INT DEFAULT 0 COMMENT '出版年份',
  `original_price` DECIMAL(10,2) DEFAULT 0.00 COMMENT '原价',
  `cover_image` VARCHAR(500) DEFAULT '' COMMENT '封面图片',
  `condition` TINYINT DEFAULT 1 COMMENT '成色等级 1-全新 2-九成 3-八成 4-七成',
  `condition_desc` VARCHAR(500) DEFAULT '' COMMENT '成色描述',
  `images` TEXT COMMENT '实拍图片JSON',
  `price` DECIMAL(10,2) NOT NULL COMMENT '售价',
  `category_id` INT DEFAULT 0 COMMENT '分类ID',
  `category_name` VARCHAR(50) DEFAULT '' COMMENT '分类名称',
  `course_id` BIGINT DEFAULT 0 COMMENT '关联课程ID',
  `course_name` VARCHAR(100) DEFAULT '' COMMENT '关联课程名称',
  `university` VARCHAR(100) DEFAULT '' COMMENT '院校',
  `stock` INT DEFAULT 1 COMMENT '库存',
  `status` TINYINT DEFAULT 1 COMMENT '状态 0-下架 1-上架',
  `description` TEXT COMMENT '描述',
  `sales` INT DEFAULT 0 COMMENT '销量',
  `views` INT DEFAULT 0 COMMENT '浏览量',
  `favorites` INT DEFAULT 0 COMMENT '收藏量',
  `price_evaluation` TINYINT DEFAULT 2 COMMENT '价格评估 1-偏低 2-合理 3-偏高',
  `suggested_price_min` DECIMAL(10,2) DEFAULT 0.00 COMMENT '建议售价下限',
  `suggested_price_max` DECIMAL(10,2) DEFAULT 0.00 COMMENT '建议售价上限',
  `is_favorited` TINYINT DEFAULT 0 COMMENT '是否已收藏',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_seller_id` (`seller_id`),
  INDEX `idx_category_id` (`category_id`),
  INDEX `idx_status` (`status`),
  FULLTEXT INDEX `ft_title_author` (`title`, `author`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教材表';

CREATE TABLE IF NOT EXISTS `cart` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `book_id` BIGINT NOT NULL COMMENT '教材ID',
  `quantity` INT DEFAULT 1 COMMENT '数量',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_user_book` (`user_id`, `book_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

CREATE TABLE IF NOT EXISTS `orders` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` VARCHAR(50) NOT NULL COMMENT '订单编号',
  `buyer_id` BIGINT NOT NULL COMMENT '买家ID',
  `buyer_name` VARCHAR(50) DEFAULT '' COMMENT '买家名称',
  `buyer_phone` VARCHAR(20) DEFAULT '' COMMENT '买家手机号',
  `seller_id` BIGINT NOT NULL COMMENT '卖家ID',
  `seller_name` VARCHAR(50) DEFAULT '' COMMENT '卖家名称',
  `seller_avatar` VARCHAR(255) DEFAULT '' COMMENT '卖家头像',
  `total_amount` DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
  `pay_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '实付金额',
  `delivery_method` TINYINT DEFAULT 3 COMMENT '配送方式 1-校内自取 2-同城配送 3-快递',
  `address` VARCHAR(500) DEFAULT '' COMMENT '收货地址',
  `pickup_point` VARCHAR(200) DEFAULT '' COMMENT '自取点',
  `pay_method` VARCHAR(20) DEFAULT '' COMMENT '支付方式',
  `status` TINYINT DEFAULT 1 COMMENT '订单状态 1-待付款 2-待发货 3-待自取 4-已发货 5-待收货 6-已完成 7-已关闭 8-退款中 9-已退款',
  `remark` VARCHAR(500) DEFAULT '' COMMENT '备注',
  `logistics_no` VARCHAR(50) DEFAULT '' COMMENT '物流单号',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间',
  `ship_time` DATETIME DEFAULT NULL COMMENT '发货时间',
  `complete_time` DATETIME DEFAULT NULL COMMENT '完成时间',
  `close_time` DATETIME DEFAULT NULL COMMENT '关闭时间',
  `refund_reason` VARCHAR(500) DEFAULT '' COMMENT '退款原因',
  `refund_description` TEXT COMMENT '退款描述',
  `refund_images` TEXT COMMENT '退款凭证',
  `refund_time` DATETIME DEFAULT NULL COMMENT '退款申请时间',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_order_no` (`order_no`),
  INDEX `idx_buyer_id` (`buyer_id`),
  INDEX `idx_seller_id` (`seller_id`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

CREATE TABLE IF NOT EXISTS `order_item` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单项ID',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `book_id` BIGINT NOT NULL COMMENT '教材ID',
  `title` VARCHAR(200) NOT NULL COMMENT '书名',
  `cover_image` VARCHAR(500) DEFAULT '' COMMENT '封面图片',
  `price` DECIMAL(10,2) NOT NULL COMMENT '单价',
  `quantity` INT DEFAULT 1 COMMENT '数量',
  `condition` TINYINT DEFAULT 1 COMMENT '成色',
  PRIMARY KEY (`id`),
  INDEX `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单项表';

CREATE TABLE IF NOT EXISTS `address` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `name` VARCHAR(50) NOT NULL COMMENT '收货人',
  `phone` VARCHAR(20) NOT NULL COMMENT '手机号',
  `province` VARCHAR(50) DEFAULT '' COMMENT '省份',
  `city` VARCHAR(50) DEFAULT '' COMMENT '城市',
  `district` VARCHAR(50) DEFAULT '' COMMENT '区县',
  `detail` VARCHAR(500) NOT NULL COMMENT '详细地址',
  `is_default` TINYINT DEFAULT 0 COMMENT '是否默认 0-否 1-是',
  `status` TINYINT DEFAULT 1 COMMENT '状态 0-删除 1-正常',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收货地址表';

CREATE TABLE IF NOT EXISTS `favorites` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `book_id` BIGINT NOT NULL COMMENT '教材ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_user_book` (`user_id`, `book_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

CREATE TABLE IF NOT EXISTS `review` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `user_id` BIGINT NOT NULL COMMENT '评价用户ID',
  `book_id` BIGINT NOT NULL COMMENT '教材ID',
  `rating` TINYINT DEFAULT 5 COMMENT '评分 1-5',
  `content` TEXT COMMENT '评价内容',
  `images` TEXT COMMENT '评价图片',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_order_id` (`order_id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_book_id` (`book_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价表';

CREATE TABLE IF NOT EXISTS `message` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `type` VARCHAR(20) DEFAULT '' COMMENT '消息类型',
  `title` VARCHAR(200) DEFAULT '' COMMENT '消息标题',
  `content` TEXT COMMENT '消息内容',
  `is_read` TINYINT DEFAULT 0 COMMENT '是否已读 0-否 1-是',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_is_read` (`is_read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息表';

CREATE TABLE IF NOT EXISTS `course` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '课程ID',
  `university` VARCHAR(100) NOT NULL COMMENT '院校',
  `major` VARCHAR(100) NOT NULL COMMENT '专业',
  `name` VARCHAR(200) NOT NULL COMMENT '课程名称',
  `teacher` VARCHAR(50) DEFAULT '' COMMENT '教师',
  `credit` DECIMAL(3,1) DEFAULT 0.0 COMMENT '学分',
  `book_title` VARCHAR(200) DEFAULT '' COMMENT '教材名称',
  `book_isbn` VARCHAR(20) DEFAULT '' COMMENT '教材ISBN',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_university` (`university`),
  INDEX `idx_university_major` (`university`, `major`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

CREATE TABLE IF NOT EXISTS `course_book` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `course_id` BIGINT NOT NULL COMMENT '课程ID',
  `book_id` BIGINT NOT NULL COMMENT '教材ID',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_course_book` (`course_id`, `book_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程-教材关联表';

CREATE TABLE IF NOT EXISTS `wallet` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '钱包ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `balance` DECIMAL(12,2) DEFAULT 0.00 COMMENT '余额',
  `freeze_amount` DECIMAL(12,2) DEFAULT 0.00 COMMENT '冻结金额',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='钱包表';

CREATE TABLE IF NOT EXISTS `wallet_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `type` VARCHAR(20) DEFAULT '' COMMENT '类型 income-收入 expense-支出',
  `type_text` VARCHAR(50) DEFAULT '' COMMENT '类型描述',
  `amount` DECIMAL(10,2) NOT NULL COMMENT '金额',
  `balance` DECIMAL(12,2) NOT NULL COMMENT '余额',
  `remark` VARCHAR(500) DEFAULT '' COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='钱包收支记录表';

INSERT INTO `sys_config` (`config_id`, `config_name`, `config_key`, `config_value`, `config_type`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) 
VALUES (100, '是否开启用户注册', 'sys.account.registerUser', 'true', 'Y', 'admin', NOW(), 'admin', NOW(), '是否开启用户注册');
