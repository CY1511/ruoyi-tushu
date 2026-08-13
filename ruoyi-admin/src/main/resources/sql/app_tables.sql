-- 商品表增加课程名称和ISBN字段
ALTER TABLE idle_goods ADD COLUMN course_name VARCHAR(100) DEFAULT NULL COMMENT '课程名称' AFTER quality_level;
ALTER TABLE idle_goods ADD COLUMN isbn VARCHAR(20) DEFAULT NULL COMMENT 'ISBN编号' AFTER course_name;

-- 收藏表
CREATE TABLE idle_favorite (
    favorite_id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
    user_id BIGINT(20) NOT NULL COMMENT '用户ID',
    goods_id BIGINT(20) NOT NULL COMMENT '商品ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    PRIMARY KEY (favorite_id),
    UNIQUE KEY uk_user_goods (user_id, goods_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品收藏表';
