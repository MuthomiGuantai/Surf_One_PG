package com.surfonepg.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "surfonepg")
public class SurfOnePgProperties {

    private String nasIdentifier;
    private int paymentTimeoutMinutes = 5;

    public String getNasIdentifier() { return nasIdentifier; }
    public void setNasIdentifier(String nasIdentifier) { this.nasIdentifier = nasIdentifier; }

    public int getPaymentTimeoutMinutes() { return paymentTimeoutMinutes; }
    public void setPaymentTimeoutMinutes(int paymentTimeoutMinutes) { this.paymentTimeoutMinutes = paymentTimeoutMinutes; }
}
