-- =============================================================================
-- 若依书铺 · 业务表索引 v1.0.1（增量脚本）
-- 用途：对**已经存在数据**的业务库补索引；全新部署直接用 v1.0.0 即可（索引已内置）
-- 前置：v1.0.0_business_tables.sql
--
-- 建索引思路（面试要能讲清）：
--   索引按「查询条件的前缀顺序」建，不是为了凑数：
--   1. idle_order(buyer_id, order_status) —— "我的订单 + 按状态筛选"是最频繁的 C 端查询，
--      等值列在前、状态列在后，能同时命中用户定位与状态过滤，不必回表后再过滤。
--   2. idle_order(seller_id, order_status) —— 卖家侧同构查询。
--   3. idle_goods(publisher_id) —— "我发布的商品"列表。
--   4. idle_goods(goods_status) / (audit_status) 分开建 —— 管理端按审核状态筛选、
--      C 端按上架状态筛选是两个独立场景，拆开比复合更实用。
--   5. idle_favorite(user_id, goods_id) 唯一索引 —— 业务上防重复收藏，
--      前缀顺便承担 user_id 单列查询，不必再单独建 user_id 索引。
-- =============================================================================

SET NAMES utf8mb4;

-- 用存储过程判断"表存在"且"索引不存在"再建，脚本可重复执行、且不依赖执行顺序
DROP PROCEDURE IF EXISTS `add_index_if_absent`;
DELIMITER $$
CREATE PROCEDURE `add_index_if_absent`(IN p_table VARCHAR(64), IN p_index VARCHAR(64), IN p_cols VARCHAR(255))
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.tables
    WHERE table_schema = DATABASE() AND table_name = p_table
  )
  AND NOT EXISTS (
    SELECT 1 FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = p_table AND index_name = p_index
  ) THEN
    SET @ddl = CONCAT('ALTER TABLE `', p_table, '` ADD INDEX `', p_index, '` (', p_cols, ')');
    PREPARE stmt FROM @ddl;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
  END IF;
END$$
DELIMITER ;

CALL `add_index_if_absent`('idle_goods',    'idx_category',        '`category_id`');
CALL `add_index_if_absent`('idle_goods',    'idx_publisher',       '`publisher_id`');
CALL `add_index_if_absent`('idle_goods',    'idx_audit',           '`audit_status`');
CALL `add_index_if_absent`('idle_goods',    'idx_status',          '`goods_status`');
CALL `add_index_if_absent`('idle_goods',    'idx_create_time',     '`create_time`');

CALL `add_index_if_absent`('idle_order',    'uk_order_no',         '`order_no`');
CALL `add_index_if_absent`('idle_order',    'idx_buyer_status',    '`buyer_id`,`order_status`');
CALL `add_index_if_absent`('idle_order',    'idx_seller_status',   '`seller_id`,`order_status`');
CALL `add_index_if_absent`('idle_order',    'idx_goods',           '`goods_id`');
CALL `add_index_if_absent`('idle_order',    'idx_create_time',     '`create_time`');

CALL `add_index_if_absent`('idle_favorite', 'uk_user_goods',       '`user_id`,`goods_id`');
CALL `add_index_if_absent`('idle_favorite', 'idx_goods',           '`goods_id`');
CALL `add_index_if_absent`('idle_category', 'idx_parent_id',       '`parent_id`');
CALL `add_index_if_absent`('idle_category', 'idx_status',          '`status`');

CALL `add_index_if_absent`('idle_order_status_log', 'idx_order_time', '`order_id`,`create_time`');

DROP PROCEDURE IF EXISTS `add_index_if_absent`;
