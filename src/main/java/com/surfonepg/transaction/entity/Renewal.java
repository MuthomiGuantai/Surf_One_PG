package com.surfonepg.transaction.entity;

import com.surfonepg.user.entity.Subscription;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "renewals")
public class Renewal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subscription_id", nullable = false)
    private Subscription subscription;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "previous_subscription_id")
    private Subscription previousSubscription;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "payment_transaction_id", nullable = false)
    private PaymentTransaction paymentTransaction;

    @Column(name = "renewed_at", nullable = false)
    private LocalDateTime renewedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    protected Renewal() {}

    public Renewal(Subscription subscription, Subscription previousSubscription, PaymentTransaction paymentTransaction) {
        this.subscription = subscription;
        this.previousSubscription = previousSubscription;
        this.paymentTransaction = paymentTransaction;
        this.renewedAt = LocalDateTime.now();
    }

    public Renewal(Subscription subscription, PaymentTransaction paymentTransaction) {
        this.subscription = subscription;
        this.paymentTransaction = paymentTransaction;
        this.renewedAt = LocalDateTime.now();
    }

    // Getters
    public Long getId() { return id; }
    public Subscription getSubscription() { return subscription; }
    public Subscription getPreviousSubscription() { return previousSubscription; }
    public PaymentTransaction getPaymentTransaction() { return paymentTransaction; }
    public LocalDateTime getRenewedAt() { return renewedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}

