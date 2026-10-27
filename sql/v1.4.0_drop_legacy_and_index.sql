-- =============================================================================
-- 若依书铺 · 移除二代模型 + 修正商品列表索引 v1.4.0（增量脚本）
-- 前置：v1.0.0_business_tables.sql、v1.0.1_business_index.sql
--
-- 这个脚本做三件事，都可以重复执行：
--
--   一、DROP 掉早期版本的 book/course/cart/orders/user 一套表。
--       对应的 Java 代码（Controller/Service/Mapper/domain）在同一次提交里已经删除，
--       留着表只会让人以为"这套模型还能用"。注意其中 user 表带着一套
--       明文口令比对的登录口（/user/login），删表是把这个旁路认证一起收掉。
--       wallet / wallet_record 保留 —— 那是规划中的功能，只是还没写 Service。
--
--   二、清理孤儿收藏。商品的删除是物理删除，而 idle_favorite 没有外键约束，
--       历史上删过商品就会留下 join 不出商品的收藏行（收藏列表显示空白行）。
--       代码侧已在 IdleGoodsServiceImpl 的删除路径补了级联清理，这里补历史数据。
--
--   三、给 idle_goods 补复合索引 (audit_status, goods_status, create_time)。
--       C 端商品列表的固定形态是 WHERE audit_status=? AND goods_status=?
--       ORDER BY create_time DESC，两个等值列 + 一个排序列正好是复合索引的教科书场景，
--       比 v1.0.1 里"拆开建"的判断更贴合实际查询；同时删掉被复合索引前缀覆盖的
--       单列 idx_audit，避免冗余。idx_status 保留，管理端存在只按上架状态筛选的查询。
-- =============================================================================

SET NAMES utf8mb4;

-- ---------------------------------------------------------------- 一、二代业务表
DROP TABLE IF EXISTS `user`;
DROP TABLE IF EXISTS `category`;
DROP TABLE IF EXISTS `book`;
DROP TABLE IF EXISTS `cart`;
DROP TABLE IF EXISTS `orders`;
DROP TABLE IF EXISTS `order_item`;
DROP TABLE IF EXISTS `address`;
DROP TABLE IF EXISTS `favorites`;
DROP TABLE IF EXISTS `review`;
DROP TABLE IF EXISTS `message`;
DROP TABLE IF EXISTS `course`;
DROP TABLE IF EXISTS `course_book`;
DROP TABLE IF EXISTS `trade`;

-- ---------------------------------------------------------------- 二、孤儿收藏
DELETE f
FROM `idle_favorite` f
LEFT JOIN `idle_goods` g ON f.goods_id = g.goods_id
WHERE g.goods_id IS NULL;

-- ---------------------------------------------------------------- 三、商品列表索引
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

DROP PROCEDURE IF EXISTS `drop_index_if_present`;
DELIMITER $$
CREATE PROCEDURE `drop_index_if_present`(IN p_table VARCHAR(64), IN p_index VARCHAR(64))
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = p_table AND index_name = p_index
  ) THEN
    SET @ddl = CONCAT('ALTER TABLE `', p_table, '` DROP INDEX `', p_index, '`');
    PREPARE stmt FROM @ddl;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
  END IF;
END$$
DELIMITER ;

CALL `add_index_if_absent`('idle_goods', 'idx_audit_status_time', '`audit_status`,`goods_status`,`create_time`');
CALL `drop_index_if_present`('idle_goods', 'idx_audit');

DROP PROCEDURE IF EXISTS `add_index_if_absent`;
DROP PROCEDURE IF EXISTS `drop_index_if_present`;
