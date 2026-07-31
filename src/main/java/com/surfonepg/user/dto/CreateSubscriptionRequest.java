package com.surfonepg.user.dto;

import jakarta.validation.constraints.NotNull;

public class CreateSubscriptionRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Package ID is required")
    private Long packageId;

    private Long paymentTransactionId;

    // Constructors
    public CreateSubscriptionRequest() {}

    public CreateSubscriptionRequest(Long userId, Long packageId) {
        this.userId = userId;
        this.packageId = packageId;
    }

    public CreateSubscriptionRequest(Long userId, Long packageId, Long paymentTransactionId) {
        this.userId = userId;
        this.packageId = packageId;
        this.paymentTransactionId = paymentTransactionId;
    }

    // Getters
    public Long getUserId() { return userId; }
    public Long getPackageId() { return packageId; }
    public Long getPaymentTransactionId() { return paymentTransactionId; }

    // Setters
    public void setUserId(Long userId) { this.userId = userId; }
    public void setPackageId(Long packageId) { this.packageId = packageId; }
    public void setPaymentTransactionId(Long paymentTransactionId) { this.paymentTransactionId = paymentTransactionId; }
}

