-- =============================================
-- 金额字段数据迁移脚本
-- 将 transaction、budget、saved_transaction 表的 amount 从元转换为分
-- 注意：请在执行此脚本前备份数据库
-- =============================================

-- 此脚本使用存储过程实现条件执行：
-- - 对于已有数据库（表存在且有数据）：执行数据迁移
-- - 对于新数据库（表不存在）：跳过执行，避免报错

-- 1. 迁移 transaction 表
DELIMITER //
CREATE PROCEDURE IF NOT EXISTS migrate_transaction_amount()
BEGIN
    -- 检查表是否存在且有数据需要迁移
    SET @table_exists = (SELECT COUNT(*) FROM information_schema.TABLES 
                          WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'transaction');
    SET @column_type = (SELECT DATA_TYPE FROM information_schema.COLUMNS 
                        WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'transaction' AND COLUMN_NAME = 'amount');
    
    -- 只有当表存在且字段类型是浮点型（需要迁移）时才执行
    IF @table_exists = 1 AND @column_type IN ('double', 'float', 'decimal') THEN
        UPDATE transaction SET amount = ROUND(amount * 100);
    END IF;
END //
DELIMITER ;

CALL migrate_transaction_amount();


-- 2. 迁移 budget 表
DELIMITER //
CREATE PROCEDURE IF NOT EXISTS migrate_budget_amount()
BEGIN
    SET @table_exists = (SELECT COUNT(*) FROM information_schema.TABLES 
                          WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'budget');
    SET @column_type = (SELECT DATA_TYPE FROM information_schema.COLUMNS 
                        WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'budget' AND COLUMN_NAME = 'amount');
    
    IF @table_exists = 1 AND @column_type IN ('double', 'float', 'decimal') THEN
        UPDATE budget SET amount = ROUND(amount * 100);
    END IF;
END //
DELIMITER ;

CALL migrate_budget_amount();


-- 3. 迁移 saved_transaction 表
DELIMITER //
CREATE PROCEDURE IF NOT EXISTS migrate_saved_transaction_amount()
BEGIN
    SET @table_exists = (SELECT COUNT(*) FROM information_schema.TABLES 
                          WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'saved_transaction');
    SET @column_type = (SELECT DATA_TYPE FROM information_schema.COLUMNS 
                        WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'saved_transaction' AND COLUMN_NAME = 'amount');
    
    IF @table_exists = 1 AND @column_type IN ('double', 'float', 'decimal') THEN
        UPDATE saved_transaction SET amount = ROUND(amount * 100);
    END IF;
END //
DELIMITER ;

CALL migrate_saved_transaction_amount();


-- 清理临时存储过程
DROP PROCEDURE IF EXISTS migrate_transaction_amount;
DROP PROCEDURE IF EXISTS migrate_budget_amount;
DROP PROCEDURE IF EXISTS migrate_saved_transaction_amount;
