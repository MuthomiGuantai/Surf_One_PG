-- Fix column type for op columns in radcheck and radreply tables
-- Change from CHAR(2) to VARCHAR(2) to match Hibernate entity mappings

ALTER TABLE radcheck MODIFY COLUMN op VARCHAR(2) NOT NULL DEFAULT '==';
ALTER TABLE radreply MODIFY COLUMN op VARCHAR(2) NOT NULL DEFAULT '=';

