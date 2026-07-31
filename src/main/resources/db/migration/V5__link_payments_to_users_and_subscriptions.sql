-- Migration V5: Link payments to users and add renewal support
-- This migration adds user tracking to payments and enables subscription renewals

ALTER TABLE payment_transactions
ADD COLUMN user_id BIGINT NULL AFTER phone_number,
ADD COLUMN subscription_id BIGINT NULL AFTER package_id,
ADD COLUMN is_renewal BOOLEAN NOT NULL DEFAULT FALSE AFTER subscription_id,
ADD CONSTRAINT fk_payment_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
ADD CONSTRAINT fk_payment_subscription FOREIGN KEY (subscription_id) REFERENCES subscriptions(id) ON DELETE SET NULL,
ADD KEY idx_user_id (user_id),
ADD KEY idx_subscription_id (subscription_id),
ADD KEY idx_renewal (is_renewal);

-- Create renewals table to track renewal history
CREATE TABLE renewals (
    id                      BIGINT AUTO_INCREMENT PRIMARY KEY,
    subscription_id         BIGINT       NOT NULL,
    previous_subscription_id BIGINT       NULL,
    payment_transaction_id  BIGINT       NOT NULL,
    renewed_at              DATETIME     NOT NULL,
    created_at              DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_renewal_subscription FOREIGN KEY (subscription_id) REFERENCES subscriptions(id) ON DELETE CASCADE,
    CONSTRAINT fk_renewal_prev_subscription FOREIGN KEY (previous_subscription_id) REFERENCES subscriptions(id) ON DELETE SET NULL,
    CONSTRAINT fk_renewal_payment FOREIGN KEY (payment_transaction_id) REFERENCES payment_transactions(id) ON DELETE CASCADE,
    KEY idx_subscription (subscription_id),
    KEY idx_payment (payment_transaction_id),
    KEY idx_renewed_at (renewed_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

