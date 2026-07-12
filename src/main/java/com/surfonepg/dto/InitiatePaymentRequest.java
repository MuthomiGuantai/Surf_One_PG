package com.surfonepg.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record InitiatePaymentRequest(

        @NotBlank
        @Pattern(regexp = "^(?:\\+254|0)7\\d{8}$|^(?:\\+254|0)1\\d{8}$", message = "Enter a valid Kenyan phone number")
        String phoneNumber,

        @NotBlank
        String packageCode
) {
}
