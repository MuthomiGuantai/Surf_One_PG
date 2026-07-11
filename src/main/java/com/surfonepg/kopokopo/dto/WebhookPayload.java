package com.surfonepg.kopokopo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WebhookPayload(
        String topic,
        String id,
        String created_at,
        Event event
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Event(String type, Resource resource) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Resource(
            String id,
            String reference,
            String status,
            String amount,
            String currency,
            String till_number,
            String sender_phone_number,
            String origination_time,
            Map<String, String> metadata
    ) {}
}
