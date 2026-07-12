package com.surfonepg.dto;

public record InitiatePaymentResponse(
        String merchantReference,
        String status,
        String message
) {
}
