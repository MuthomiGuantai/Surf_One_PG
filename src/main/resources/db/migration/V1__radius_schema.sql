-- Standard FreeRADIUS schema (subset). If FreeRADIUS is already installed with its own
-- schema.sql applied to this database, skip this migration (mark it as baseline instead).

CREATE TABLE IF NOT EXISTS radcheck (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(64)  NOT NULL DEFAULT '',
    attribute     VARCHAR(64)  NOT NULL DEFAULT '',
    op            CHAR(2)      NOT NULL DEFAULT '==',
    value         VARCHAR(253) NOT NULL DEFAULT '',
    KEY username (username(32))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS radreply (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(64)  NOT NULL DEFAULT '',
    attribute     VARCHAR(64)  NOT NULL DEFAULT '',
    op            CHAR(2)      NOT NULL DEFAULT '=',
    value         VARCHAR(253) NOT NULL DEFAULT '',
    KEY username (username(32))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS radusergroup (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(64) NOT NULL DEFAULT '',
    groupname     VARCHAR(64) NOT NULL DEFAULT '',
    priority      INT NOT NULL DEFAULT 1,
    KEY username (username(32))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS radacct (
    radacctid          BIGINT AUTO_INCREMENT PRIMARY KEY,
    acctsessionid       VARCHAR(64) NOT NULL DEFAULT '',
    acctuniqueid        VARCHAR(32) NOT NULL DEFAULT '',
    username             VARCHAR(64) NOT NULL DEFAULT '',
    nasipaddress         VARCHAR(15) NOT NULL DEFAULT '',
    nasidentifier        VARCHAR(64) DEFAULT '',
    acctstarttime        DATETIME NULL,
    acctstoptime         DATETIME NULL,
    acctsessiontime      INT UNSIGNED DEFAULT NULL,
    acctinputoctets      BIGINT DEFAULT NULL,
    acctoutputoctets     BIGINT DEFAULT NULL,
    callingstationid     VARCHAR(50) NOT NULL DEFAULT '',
    UNIQUE KEY acctuniqueid (acctuniqueid),
    KEY username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS nas (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    nasname       VARCHAR(128) NOT NULL,
    shortname     VARCHAR(32),
    type          VARCHAR(30) DEFAULT 'other',
    secret        VARCHAR(60) NOT NULL DEFAULT 'secret',
    description   VARCHAR(200)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
