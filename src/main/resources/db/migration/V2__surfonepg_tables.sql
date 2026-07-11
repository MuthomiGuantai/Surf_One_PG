CREATE TABLE data_packages (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    code              VARCHAR(32)   NOT NULL UNIQUE,
    name              VARCHAR(100)  NOT NULL,
    price_kes         DECIMAL(10,2) NOT NULL,
    duration_minutes  INT           NOT NULL,
    download_speed_kbps INT         NOT NULL,
    upload_speed_kbps   INT         NOT NULL,
    data_cap_mb       INT           NULL,
    active            BOOLEAN       NOT NULL DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO data_packages (code, name, price_kes, duration_minutes, download_speed_kbps, upload_speed_kbps, data_cap_mb) VALUES
 ('HSP',  'Hotspot Quick Access', 5.00,   30, 1024,  512,  500),
 ('HR1',  '1 Hour Surf',          10.00,   60, 2048, 1024, NULL),
 ('HR3',  '3 Hour Surf',          25.00,  180, 2048, 1024, NULL),
 ('DAY1', 'Daily Unlimited',      50.00, 1440, 4096, 2048, NULL),
 ('WK1',  'Weekly Surf',         250.00,10080, 4096, 2048, NULL),
 ('MO1',  'Monthly Unlimited',  1000.00,43200, 6144, 3072, NULL);

CREATE TABLE payment_transactions (
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    merchant_reference    VARCHAR(64)  NOT NULL UNIQUE,   -- our own idempotency key, sent as metadata.reference
    kopokopo_location_url VARCHAR(255) NULL,               -- Location header returned from the STK push request
    kopokopo_resource_id  VARCHAR(64)  NULL,                -- resource.id from the webhook payload
    phone_number          VARCHAR(20)  NOT NULL,
    package_id            BIGINT       NOT NULL,
    amount_kes            DECIMAL(10,2) NOT NULL,
    mpesa_receipt         VARCHAR(32)  NULL,                -- reference from the webhook (Daraja-style receipt no.)
    status                VARCHAR(20)  NOT NULL DEFAULT 'PENDING', -- PENDING, RECEIVED, FAILED, EXPIRED, PROVISIONED
    radius_username       VARCHAR(64)  NULL,
    radius_password       VARCHAR(16)  NULL,               -- PIN shown/SMS'd to the customer; also stored as Cleartext-Password in radcheck
    created_at            DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_txn_package FOREIGN KEY (package_id) REFERENCES data_packages(id),
    KEY idx_phone (phone_number),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
