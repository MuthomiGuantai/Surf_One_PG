-- Migration V4: Add users and subscriptions tables
-- This migration adds support for user management and subscription tracking

CREATE TABLE users (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    phone_number      VARCHAR(20)   NOT NULL UNIQUE,
    first_name        VARCHAR(100)  NOT NULL,
    last_name         VARCHAR(100)  NOT NULL,
    email             VARCHAR(100)  UNIQUE,
    active            BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_phone (phone_number),
    KEY idx_email (email),
    KEY idx_active (active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE subscriptions (
    id                        BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id                   BIGINT       NOT NULL,
    package_id                BIGINT       NOT NULL,
    payment_transaction_id    BIGINT       NULL,
    status                    VARCHAR(20)  NOT NULL DEFAULT 'PENDING',  -- ACTIVE, EXPIRED, CANCELLED, PENDING
    activated_at              DATETIME     NULL,
    expires_at                DATETIME     NULL,
    created_at                DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_sub_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_sub_package FOREIGN KEY (package_id) REFERENCES data_packages(id) ON DELETE RESTRICT,
    CONSTRAINT fk_sub_transaction FOREIGN KEY (payment_transaction_id) REFERENCES payment_transactions(id) ON DELETE SET NULL,
    KEY idx_user (user_id),
    KEY idx_package (package_id),
    KEY idx_status (status),
    KEY idx_expires_at (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

