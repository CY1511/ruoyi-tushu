-- 补齐业务表标准字段（若已存在则忽略错误）

-- idle_goods 表
ALTER TABLE idle_goods ADD COLUMN create_by VARCHAR(64) DEFAULT NULL COMMENT '创建者' AFTER collect_count;
ALTER TABLE idle_goods ADD COLUMN create_time DATETIME DEFAULT NULL COMMENT '创建时间' AFTER create_by;
ALTER TABLE idle_goods ADD COLUMN update_by VARCHAR(64) DEFAULT NULL COMMENT '更新者' AFTER create_time;
ALTER TABLE idle_goods ADD COLUMN update_time DATETIME DEFAULT NULL COMMENT '更新时间' AFTER update_by;
ALTER TABLE idle_goods ADD COLUMN remark VARCHAR(500) DEFAULT NULL COMMENT '备注' AFTER update_time;

-- idle_order 表
ALTER TABLE idle_order ADD COLUMN create_by VARCHAR(64) DEFAULT NULL COMMENT '创建者' AFTER remark;
ALTER TABLE idle_order ADD COLUMN create_time DATETIME DEFAULT NULL COMMENT '创建时间' AFTER create_by;
ALTER TABLE idle_order ADD COLUMN update_by VARCHAR(64) DEFAULT NULL COMMENT '更新者' AFTER create_time;
ALTER TABLE idle_order ADD COLUMN update_time DATETIME DEFAULT NULL COMMENT '更新时间' AFTER update_by;

-- idle_category 表
ALTER TABLE idle_category ADD COLUMN create_by VARCHAR(64) DEFAULT NULL COMMENT '创建者' AFTER status;
ALTER TABLE idle_category ADD COLUMN create_time DATETIME DEFAULT NULL COMMENT '创建时间' AFTER create_by;
ALTER TABLE idle_category ADD COLUMN update_by VARCHAR(64) DEFAULT NULL COMMENT '更新者' AFTER create_time;
ALTER TABLE idle_category ADD COLUMN update_time DATETIME DEFAULT NULL COMMENT '更新时间' AFTER update_by;
ALTER TABLE idle_category ADD COLUMN remark VARCHAR(500) DEFAULT NULL COMMENT '备注' AFTER update_time;

-- idle_favorite 表（create_time 已存在）
ALTER TABLE idle_favorite ADD COLUMN create_by VARCHAR(64) DEFAULT NULL COMMENT '创建者' AFTER create_time;
ALTER TABLE idle_favorite ADD COLUMN update_by VARCHAR(64) DEFAULT NULL COMMENT '更新者' AFTER create_by;
ALTER TABLE idle_favorite ADD COLUMN update_time DATETIME DEFAULT NULL COMMENT '更新时间' AFTER update_by;
