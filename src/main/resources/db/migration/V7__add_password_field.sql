-- Migration V7: Add password field to users and set email as unique
-- This migration adds support for password-based authentication using Basic Auth

-- Check if password column exists, only add if it doesn't
-- We use information_schema to check if column already exists
-- If the column already exists, these statements will be no-ops (safe to retry)

-- Add password column only if it doesn't exist
-- Note: We can't use IF NOT EXISTS in ALTER TABLE, so we'll use a conditional check
-- by checking the information_schema

SET @column_exists = (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_NAME = 'users' AND COLUMN_NAME = 'password' AND TABLE_SCHEMA = DATABASE()
);

-- If password column doesn't exist, add it
SET @sql = IF(@column_exists = 0, 'ALTER TABLE users ADD COLUMN password VARCHAR(255) NULL AFTER email', 'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Make email NOT NULL (will need to handle existing NULL values)
UPDATE users SET email = CONCAT('user_', id, '@example.com') WHERE email IS NULL;

-- Modify email column to be NOT NULL and UNIQUE
-- This is safe even if already NOT NULL (MySQL allows this)
ALTER TABLE users MODIFY COLUMN email VARCHAR(100) NOT NULL UNIQUE;

-- Note: idx_email index already exists from V4 migration
-- idx_phone and idx_active indexes also exist from V4


