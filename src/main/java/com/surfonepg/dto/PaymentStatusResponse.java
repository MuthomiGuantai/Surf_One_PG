package com.surfonepg.dto;

public record PaymentStatusResponse(
        String merchantReference,
        String status,
        String radiusUsername,
        String radiusPassword,
        String message
) {
}
