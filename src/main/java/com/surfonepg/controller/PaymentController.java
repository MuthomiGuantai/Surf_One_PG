package com.surfonepg.controller;

import com.surfonepg.dto.InitiatePaymentRequest;
import com.surfonepg.dto.InitiatePaymentResponse;
import com.surfonepg.dto.PaymentStatusResponse;
import com.surfonepg.transaction.PaymentService;
import com.surfonepg.transaction.entity.PaymentTransaction;
import com.surfonepg.transaction.repository.PaymentTransactionRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentTransactionRepository transactionRepository;

    public PaymentController(PaymentService paymentService, PaymentTransactionRepository transactionRepository) {
        this.paymentService = paymentService;
        this.transactionRepository = transactionRepository;
    }

    /** Called by the captive portal when the customer picks a package and taps "Pay". */
    @PostMapping
    public ResponseEntity<InitiatePaymentResponse> initiate(@Valid @RequestBody InitiatePaymentRequest request) {
        PaymentTransaction txn = paymentService.initiate(request.phoneNumber(), request.packageCode());

        if (txn.getStatus() == PaymentTransaction.Status.FAILED) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(new InitiatePaymentResponse(
                    txn.getMerchantReference(), txn.getStatus().name(), "Could not initiate STK push. Please try again."));
        }

        return ResponseEntity.accepted().body(new InitiatePaymentResponse(
                txn.getMerchantReference(), txn.getStatus().name(), "Check your phone and enter your M-Pesa PIN."));
    }

    /** Polled by the captive portal every couple of seconds while waiting for the webhook. */
    @GetMapping("/{merchantReference}")
    public ResponseEntity<PaymentStatusResponse> status(@PathVariable String merchantReference) {
        return transactionRepository.findByMerchantReference(merchantReference)
                .map(txn -> ResponseEntity.ok(new PaymentStatusResponse(
                        txn.getMerchantReference(),
                        txn.getStatus().name(),
                        txn.getRadiusUsername(),
                        txn.getRadiusPassword(),
                        statusMessage(txn.getStatus())
                )))
                .orElse(ResponseEntity.notFound().build());
    }

    private String statusMessage(PaymentTransaction.Status status) {
        return switch (status) {
            case PENDING -> "Waiting for payment confirmation.";
            case RECEIVED -> "Payment received, setting up your access.";
            case PROVISIONED -> "You're connected! Use the username and PIN shown to log in.";
            case FAILED -> "Payment failed or was cancelled.";
            case EXPIRED -> "This payment request expired. Please try again.";
        };
    }
}
