-- FreeRADIUS schema adapted for PostgreSQL
-- This is a partial FreeRADIUS schema subset compatible with PostgreSQL

CREATE TABLE IF NOT EXISTS radcheck (
    id SERIAL PRIMARY KEY,
    username VARCHAR(64) NOT NULL DEFAULT '',
    attribute VARCHAR(64) NOT NULL DEFAULT '',
    op VARCHAR(2) NOT NULL DEFAULT '==',
    value VARCHAR(253) NOT NULL DEFAULT ''
);

CREATE INDEX IF NOT EXISTS radcheck_username_idx ON radcheck(username);

CREATE TABLE IF NOT EXISTS radreply (
    id SERIAL PRIMARY KEY,
    username VARCHAR(64) NOT NULL DEFAULT '',
    attribute VARCHAR(64) NOT NULL DEFAULT '',
    op VARCHAR(2) NOT NULL DEFAULT '=',
    value VARCHAR(253) NOT NULL DEFAULT ''
);

CREATE INDEX IF NOT EXISTS radreply_username_idx ON radreply(username);

CREATE TABLE IF NOT EXISTS radusergroup (
    id SERIAL PRIMARY KEY,
    username VARCHAR(64) NOT NULL DEFAULT '',
    groupname VARCHAR(64) NOT NULL DEFAULT '',
    priority INTEGER NOT NULL DEFAULT 1
);

CREATE INDEX IF NOT EXISTS radusergroup_username_idx ON radusergroup(username);

CREATE TABLE IF NOT EXISTS radacct (
    radacctid SERIAL PRIMARY KEY,
    acctsessionid VARCHAR(64) NOT NULL DEFAULT '',
    acctuniqueid VARCHAR(32) NOT NULL DEFAULT '',
    username VARCHAR(64) NOT NULL DEFAULT '',
    nasipaddress VARCHAR(15) NOT NULL DEFAULT '',
    nasidentifier VARCHAR(64),
    acctstarttime TIMESTAMP,
    acctstoptime TIMESTAMP,
    acctsessiontime INTEGER,
    acctinputoctets BIGINT,
    acctoutputoctets BIGINT,
    callingstationid VARCHAR(50) NOT NULL DEFAULT ''
);

CREATE UNIQUE INDEX IF NOT EXISTS radacct_acctuniqueid_idx ON radacct(acctuniqueid);
CREATE INDEX IF NOT EXISTS radacct_username_idx ON radacct(username);

CREATE TABLE IF NOT EXISTS nas (
    id SERIAL PRIMARY KEY,
    nasname VARCHAR(128) NOT NULL,
    shortname VARCHAR(32),
    type VARCHAR(30) DEFAULT 'other',
    secret VARCHAR(60) NOT NULL DEFAULT 'secret',
    description VARCHAR(200)
);

