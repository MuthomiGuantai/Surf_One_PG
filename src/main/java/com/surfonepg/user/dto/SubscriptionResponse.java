package com.surfonepg.user.dto;

import com.surfonepg.user.entity.Subscription;
import java.time.LocalDateTime;

public class SubscriptionResponse {

    private Long id;
    private Long userId;
    private String userPhoneNumber;
    private String userName;
    private Long packageId;
    private String packageCode;
    private String packageName;
    private String status;
    private LocalDateTime activatedAt;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;

    public SubscriptionResponse() {}

    public SubscriptionResponse(Subscription subscription) {
        this.id = subscription.getId();
        this.userId = subscription.getUser().getId();
        this.userPhoneNumber = subscription.getUser().getPhoneNumber();
        this.userName = subscription.getUser().getFirstName() + " " + subscription.getUser().getLastName();
        this.packageId = subscription.getDataPackage().getId();
        this.packageCode = subscription.getDataPackage().getCode();
        this.packageName = subscription.getDataPackage().getName();
        this.status = subscription.getStatus().toString();
        this.activatedAt = subscription.getActivatedAt();
        this.expiresAt = subscription.getExpiresAt();
        this.createdAt = subscription.getCreatedAt();
    }

    // Getters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getUserPhoneNumber() { return userPhoneNumber; }
    public String getUserName() { return userName; }
    public Long getPackageId() { return packageId; }
    public String getPackageCode() { return packageCode; }
    public String getPackageName() { return packageName; }
    public String getStatus() { return status; }
    public LocalDateTime getActivatedAt() { return activatedAt; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setUserPhoneNumber(String userPhoneNumber) { this.userPhoneNumber = userPhoneNumber; }
    public void setUserName(String userName) { this.userName = userName; }
    public void setPackageId(Long packageId) { this.packageId = packageId; }
    public void setPackageCode(String packageCode) { this.packageCode = packageCode; }
    public void setPackageName(String packageName) { this.packageName = packageName; }
    public void setStatus(String status) { this.status = status; }
    public void setActivatedAt(LocalDateTime activatedAt) { this.activatedAt = activatedAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

