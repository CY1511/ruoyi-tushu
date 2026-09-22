-- =============================================================================
-- 若依书铺 · 订单状态流转日志表 v1.2.0
-- 前置：v1.0.0_business_tables.sql（依赖 idle_order）
--
-- 为什么需要这张表：
--   idle_order.order_status 只保存"当前状态"，一旦被改就没有痕迹。
--   交易纠纷、客服排查、数据统计都需要回答"谁在什么时候把订单从 A 改成了 B"，
--   因此每次合法流转都落一条日志，与订单更新在同一个事务里。
-- =============================================================================

SET NAMES utf8mb4;

DROP TABLE IF EXISTS `idle_order_status_log`;
CREATE TABLE `idle_order_status_log` (
  `log_id`        BIGINT(20)   NOT NULL AUTO_INCREMENT            COMMENT '日志ID',
  `order_id`      BIGINT(20)   NOT NULL                           COMMENT '订单ID',
  `order_no`      VARCHAR(64)  DEFAULT ''                         COMMENT '订单编号（冗余，便于排查）',
  `from_status`   VARCHAR(2)   DEFAULT NULL                       COMMENT '变更前状态',
  `to_status`     VARCHAR(2)   NOT NULL                           COMMENT '变更后状态',
  `operator_id`   BIGINT(20)   DEFAULT NULL                       COMMENT '操作人ID',
  `operator_name` VARCHAR(64)  DEFAULT ''                         COMMENT '操作人名称',
  `operator_role` VARCHAR(16)  DEFAULT ''                         COMMENT '操作人角色：BUYER/SELLER/ADMIN/SYSTEM',
  `remark`        VARCHAR(500) DEFAULT ''                         COMMENT '备注（如取消原因）',
  `create_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP          COMMENT '操作时间',
  PRIMARY KEY (`log_id`),
  KEY `idx_order_time` (`order_id`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单状态流转日志表';
