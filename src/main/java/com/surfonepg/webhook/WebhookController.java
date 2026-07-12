package com.surfonepg.webhook;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.surfonepg.kopokopo.dto.WebhookPayload;
import com.surfonepg.transaction.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * KopoKopo POSTs here for buygoods_transaction_received / incoming_payment events once
 * the customer has entered their M-Pesa PIN and the payment has settled (or failed).
 * Configure this exact URL as the callback_url when initiating the STK push, and it must
 * be reachable from the internet (use ngrok in sandbox, same as your other Daraja work).
 */
@RestController
@RequestMapping("/api/v1/webhooks")
public class WebhookController {

    private static final Logger log = LoggerFactory.getLogger(WebhookController.class);

    private final WebhookSignatureVerifier signatureVerifier;
    private final PaymentService paymentService;
    private final ObjectMapper objectMapper;

    public WebhookController(WebhookSignatureVerifier signatureVerifier, PaymentService paymentService, ObjectMapper objectMapper) {
        this.signatureVerifier = signatureVerifier;
        this.paymentService = paymentService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/kopokopo")
    public ResponseEntity<Void> receive(HttpServletRequest request) throws IOException {
        String rawBody = new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        String signature = request.getHeader("X-KopoKopo-Signature");

        if (!signatureVerifier.isValid(rawBody, signature)) {
            log.warn("Rejected webhook with invalid signature");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        WebhookPayload payload = objectMapper.readValue(rawBody, WebhookPayload.class);
        log.info("Received KopoKopo webhook: topic={} id={}", payload.topic(), payload.id());

        paymentService.handleWebhook(payload);

        // KopoKopo only cares about the HTTP status; always 200 once we've accepted the payload
        // so it doesn't retry indefinitely, even if our internal matching logic logged a warning.
        return ResponseEntity.ok().build();
    }
}
