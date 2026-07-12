package com.surfonepg.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kopokopo")
public class KopoKopoProperties {

    /** e.g. https://sandbox.kopokopo.com or https://api.kopokopo.com in production */
    private String baseUrl;
    private String clientId;
    private String clientSecret;
    private String tillNumber;
    private String paymentChannel = "M-PESA STK Push";
    private String callbackUrl;

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public String getClientSecret() { return clientSecret; }
    public void setClientSecret(String clientSecret) { this.clientSecret = clientSecret; }

    public String getTillNumber() { return tillNumber; }
    public void setTillNumber(String tillNumber) { this.tillNumber = tillNumber; }

    public String getPaymentChannel() { return paymentChannel; }
    public void setPaymentChannel(String paymentChannel) { this.paymentChannel = paymentChannel; }

    public String getCallbackUrl() { return callbackUrl; }
    public void setCallbackUrl(String callbackUrl) { this.callbackUrl = callbackUrl; }
}
