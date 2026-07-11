package com.surfonepg.kopokopo;

import com.surfonepg.config.KopoKopoProperties;
import com.surfonepg.kopokopo.dto.StkPushRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class KopoKopoStkService {

    private final RestClient restClient;
    private final KopoKopoAuthService authService;
    private final KopoKopoProperties props;

    public KopoKopoStkService(RestClient kopoKopoRestClient, KopoKopoAuthService authService, KopoKopoProperties props) {
        this.restClient = kopoKopoRestClient;
        this.authService = authService;
        this.props = props;
    }

    /**
     * Initiates an M-Pesa STK push and returns the "Location" URL KopoKopo
     * assigns to the created incoming_payment resource (useful for polling / audit).
     *
     * @param phoneNumber     subscriber phone in international format, e.g. +254712345678
     * @param amountKes       amount as a plain decimal string, e.g. "50.00"
     * @param merchantReference our own idempotency key, stored as metadata.reference
     */
    public String initiateStkPush(String phoneNumber, String amountKes, String merchantReference) {
        String token = authService.getAccessToken();

        StkPushRequest request = new StkPushRequest(
                props.getPaymentChannel(),
                props.getTillNumber(),
                new StkPushRequest.Subscriber(null, null, phoneNumber, null),
                new StkPushRequest.Amount("KES", amountKes),
                Map.of(
                        "reference", merchantReference,
                        "notes", "Surf One PG hotspot access"
                ),
                new StkPushRequest.Links(props.getCallbackUrl())
        );

        ResponseEntity<Void> response = restClient.post()
                .uri("/api/v2/incoming_payments")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(request)
                .retrieve()
                .toBodilessEntity();

        HttpHeaders headers = response.getHeaders();
        String location = headers.getFirst(HttpHeaders.LOCATION);
        if (location == null) {
            throw new IllegalStateException("KopoKopo STK push did not return a Location header");
        }
        return location;
    }
}
