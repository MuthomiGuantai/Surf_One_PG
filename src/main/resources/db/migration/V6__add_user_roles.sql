-- Migration V6: Add user roles for role-based access control
-- This migration adds support for role-based authorization (ADMIN, CLIENT)

ALTER TABLE users
ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'CLIENT' AFTER active;

-- Create an index on the role column for faster lookups
CREATE INDEX idx_role ON users(role);

-- You can optionally set the first user (or specific users) as ADMIN
-- UPDATE users SET role = 'ADMIN' WHERE id = 1 LIMIT 1;

