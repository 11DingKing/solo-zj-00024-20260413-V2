-- =============================================
-- 金额字段数据迁移脚本
-- 将 transaction 表的 amount 从元转换为分
-- 注意：请在执行此脚本前备份数据库
-- =============================================

-- 1. 备份现有数据（可选但建议执行）
-- CREATE TABLE transaction_backup_20260424 AS SELECT * FROM transaction;

-- 2. 更新 transaction 表：将 amount 从元转换为分（乘以 100）
-- 使用 ROUND 确保正确四舍五入
UPDATE transaction SET amount = ROUND(amount * 100);

-- 3. 验证更新结果（可选）
-- SELECT amount FROM transaction LIMIT 10;
