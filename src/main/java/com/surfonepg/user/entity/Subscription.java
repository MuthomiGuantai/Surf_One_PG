package com.surfonepg.user.entity;

import com.surfonepg.packages.entity.DataPackage;
import com.surfonepg.transaction.entity.PaymentTransaction;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "subscriptions")
public class Subscription {

    public enum Status { ACTIVE, EXPIRED, CANCELLED, PENDING }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "package_id", nullable = false)
    private DataPackage dataPackage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_transaction_id", nullable = true)
    private PaymentTransaction paymentTransaction;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;

    @Column(name = "activated_at")
    private LocalDateTime activatedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    protected Subscription() {}

    public Subscription(User user, DataPackage dataPackage) {
        this.user = user;
        this.dataPackage = dataPackage;
        this.status = Status.PENDING;
    }

    public Subscription(User user, DataPackage dataPackage, PaymentTransaction paymentTransaction) {
        this.user = user;
        this.dataPackage = dataPackage;
        this.paymentTransaction = paymentTransaction;
        this.status = Status.PENDING;
    }

    @PreUpdate
    void onUpdate() { this.updatedAt = LocalDateTime.now(); }

    // Getters
    public Long getId() { return id; }
    public User getUser() { return user; }
    public DataPackage getDataPackage() { return dataPackage; }
    public PaymentTransaction getPaymentTransaction() { return paymentTransaction; }
    public Status getStatus() { return status; }
    public LocalDateTime getActivatedAt() { return activatedAt; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Setters
    public void setStatus(Status status) { this.status = status; }
    public void setActivatedAt(LocalDateTime activatedAt) { this.activatedAt = activatedAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    public void setPaymentTransaction(PaymentTransaction paymentTransaction) {
        this.paymentTransaction = paymentTransaction;
    }
}

