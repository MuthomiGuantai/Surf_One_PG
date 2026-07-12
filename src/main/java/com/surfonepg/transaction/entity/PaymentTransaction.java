package com.surfonepg.transaction.entity;

import com.surfonepg.packages.entity.DataPackage;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_transactions")
public class PaymentTransaction {

    public enum Status { PENDING, RECEIVED, FAILED, EXPIRED, PROVISIONED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "merchant_reference", nullable = false, unique = true)
    private String merchantReference;

    @Column(name = "kopokopo_location_url")
    private String kopoKopoLocationUrl;

    @Column(name = "kopokopo_resource_id")
    private String kopoKopoResourceId;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id", nullable = false)
    private DataPackage dataPackage;

    @Column(name = "amount_kes", nullable = false)
    private BigDecimal amountKes;

    @Column(name = "mpesa_receipt")
    private String mpesaReceipt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;

    @Column(name = "radius_username")
    private String radiusUsername;

    @Column(name = "radius_password")
    private String radiusPassword;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    protected PaymentTransaction() {}

    public PaymentTransaction(String merchantReference, String phoneNumber, DataPackage dataPackage, BigDecimal amountKes) {
        this.merchantReference = merchantReference;
        this.phoneNumber = phoneNumber;
        this.dataPackage = dataPackage;
        this.amountKes = amountKes;
    }

    @PreUpdate
    void onUpdate() { this.updatedAt = LocalDateTime.now(); }

    public Long getId() { return id; }
    public String getMerchantReference() { return merchantReference; }
    public String getKopoKopoLocationUrl() { return kopoKopoLocationUrl; }
    public void setKopoKopoLocationUrl(String v) { this.kopoKopoLocationUrl = v; }
    public String getKopoKopoResourceId() { return kopoKopoResourceId; }
    public void setKopoKopoResourceId(String v) { this.kopoKopoResourceId = v; }
    public String getPhoneNumber() { return phoneNumber; }
    public DataPackage getDataPackage() { return dataPackage; }
    public BigDecimal getAmountKes() { return amountKes; }
    public String getMpesaReceipt() { return mpesaReceipt; }
    public void setMpesaReceipt(String v) { this.mpesaReceipt = v; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public String getRadiusUsername() { return radiusUsername; }
    public void setRadiusUsername(String v) { this.radiusUsername = v; }
    public String getRadiusPassword() { return radiusPassword; }
    public void setRadiusPassword(String v) { this.radiusPassword = v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
