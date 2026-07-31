package com.surfonepg.transaction.dto;

import com.surfonepg.transaction.entity.Renewal;
import java.time.LocalDateTime;

public class RenewalResponse {

    private Long id;
    private Long subscriptionId;
    private String packageCode;
    private String packageName;
    private String userPhoneNumber;
    private String userName;
    private Long previousSubscriptionId;
    private Long paymentTransactionId;
    private LocalDateTime renewedAt;
    private LocalDateTime createdAt;

    public RenewalResponse() {}

    public RenewalResponse(Renewal renewal) {
        this.id = renewal.getId();
        this.subscriptionId = renewal.getSubscription().getId();
        this.packageCode = renewal.getSubscription().getDataPackage().getCode();
        this.packageName = renewal.getSubscription().getDataPackage().getName();
        this.userPhoneNumber = renewal.getSubscription().getUser().getPhoneNumber();
        this.userName = renewal.getSubscription().getUser().getFirstName() + " " + renewal.getSubscription().getUser().getLastName();
        this.previousSubscriptionId = renewal.getPreviousSubscription() != null ? renewal.getPreviousSubscription().getId() : null;
        this.paymentTransactionId = renewal.getPaymentTransaction().getId();
        this.renewedAt = renewal.getRenewedAt();
        this.createdAt = renewal.getCreatedAt();
    }

    // Getters
    public Long getId() { return id; }
    public Long getSubscriptionId() { return subscriptionId; }
    public String getPackageCode() { return packageCode; }
    public String getPackageName() { return packageName; }
    public String getUserPhoneNumber() { return userPhoneNumber; }
    public String getUserName() { return userName; }
    public Long getPreviousSubscriptionId() { return previousSubscriptionId; }
    public Long getPaymentTransactionId() { return paymentTransactionId; }
    public LocalDateTime getRenewedAt() { return renewedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setSubscriptionId(Long subscriptionId) { this.subscriptionId = subscriptionId; }
    public void setPackageCode(String packageCode) { this.packageCode = packageCode; }
    public void setPackageName(String packageName) { this.packageName = packageName; }
    public void setUserPhoneNumber(String userPhoneNumber) { this.userPhoneNumber = userPhoneNumber; }
    public void setUserName(String userName) { this.userName = userName; }
    public void setPreviousSubscriptionId(Long previousSubscriptionId) { this.previousSubscriptionId = previousSubscriptionId; }
    public void setPaymentTransactionId(Long paymentTransactionId) { this.paymentTransactionId = paymentTransactionId; }
    public void setRenewedAt(LocalDateTime renewedAt) { this.renewedAt = renewedAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

