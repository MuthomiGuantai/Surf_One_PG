package com.surfonepg.kopokopo.dto;

import java.util.Map;

public record StkPushRequest(
        String payment_channel,
        String till_number,
        Subscriber subscriber,
        Amount amount,
        Map<String, String> metadata,
        Links _links
) {
    public record Subscriber(String first_name, String last_name, String phone_number, String email) {}
    public record Amount(String currency, String value) {}
    public record Links(String callback_url) {}
}
