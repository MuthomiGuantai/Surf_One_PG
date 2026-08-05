package com.surfonepg.user.controller;

import com.surfonepg.user.entity.Subscription;
import com.surfonepg.user.dto.CreateSubscriptionRequest;
import com.surfonepg.user.dto.SubscriptionResponse;
import com.surfonepg.user.service.SubscriptionService;
import com.surfonepg.transaction.dto.RenewalResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
    public ResponseEntity<SubscriptionResponse> createSubscription(
            @Valid @RequestBody CreateSubscriptionRequest request
    ) {
        try {
            Subscription subscription = subscriptionService.createSubscription(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(new SubscriptionResponse(subscription));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
    public ResponseEntity<SubscriptionResponse> getSubscriptionById(@PathVariable Long id) {
        return subscriptionService.getSubscriptionById(id)
            .map(subscription -> ResponseEntity.ok(new SubscriptionResponse(subscription)))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
    public ResponseEntity<List<SubscriptionResponse>> getUserSubscriptions(@PathVariable Long userId) {
        try {
            List<SubscriptionResponse> subscriptions = subscriptionService.getUserSubscriptions(userId)
                .stream()
                .map(SubscriptionResponse::new)
                .collect(Collectors.toList());
            return ResponseEntity.ok(subscriptions);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}/active")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
    public ResponseEntity<List<SubscriptionResponse>> getUserActiveSubscriptions(@PathVariable Long userId) {
        try {
            List<SubscriptionResponse> subscriptions = subscriptionService.getUserActiveSubscriptions(userId)
                .stream()
                .map(SubscriptionResponse::new)
                .collect(Collectors.toList());
            return ResponseEntity.ok(subscriptions);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/package/{packageId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SubscriptionResponse>> getSubscriptionsByPackage(@PathVariable Long packageId) {
        try {
            List<SubscriptionResponse> subscriptions = subscriptionService.getSubscriptionsByPackage(packageId)
                .stream()
                .map(SubscriptionResponse::new)
                .collect(Collectors.toList());
            return ResponseEntity.ok(subscriptions);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SubscriptionResponse>> getSubscriptionsByStatus(@PathVariable String status) {
        try {
            Subscription.Status statusEnum = Subscription.Status.valueOf(status.toUpperCase());
            List<SubscriptionResponse> subscriptions = subscriptionService.getSubscriptionsByStatus(statusEnum)
                .stream()
                .map(SubscriptionResponse::new)
                .collect(Collectors.toList());
            return ResponseEntity.ok(subscriptions);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SubscriptionResponse>> getAllSubscriptions() {
        List<SubscriptionResponse> subscriptions = subscriptionService.getAllSubscriptions()
            .stream()
            .map(SubscriptionResponse::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(subscriptions);
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SubscriptionResponse> activateSubscription(@PathVariable Long id) {
        try {
            Subscription subscription = subscriptionService.activateSubscription(id);
            return ResponseEntity.ok(new SubscriptionResponse(subscription));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/expire")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SubscriptionResponse> expireSubscription(@PathVariable Long id) {
        try {
            Subscription subscription = subscriptionService.expireSubscription(id);
            return ResponseEntity.ok(new SubscriptionResponse(subscription));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SubscriptionResponse> cancelSubscription(@PathVariable Long id) {
        try {
            Subscription subscription = subscriptionService.cancelSubscription(id);
            return ResponseEntity.ok(new SubscriptionResponse(subscription));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/expire-expired")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> expireExpiredSubscriptions() {
        subscriptionService.expireExpiredSubscriptions();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{userId}/has-active/{packageId}")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
    public ResponseEntity<Boolean> hasActiveSubscription(
            @PathVariable Long userId,
            @PathVariable Long packageId
    ) {
        try {
            boolean hasActive = subscriptionService.hasActiveSubscription(userId, packageId);
            return ResponseEntity.ok(hasActive);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{subscriptionId}/renewals")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
    public ResponseEntity<List<RenewalResponse>> getSubscriptionRenewals(@PathVariable Long subscriptionId) {
        try {
            List<RenewalResponse> renewals = subscriptionService.getSubscriptionRenewals(subscriptionId)
                .stream()
                .map(RenewalResponse::new)
                .collect(Collectors.toList());
            return ResponseEntity.ok(renewals);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}/renewals")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
    public ResponseEntity<List<RenewalResponse>> getUserRenewals(@PathVariable Long userId) {
        try {
            List<RenewalResponse> renewals = subscriptionService.getUserRenewals(userId)
                .stream()
                .map(RenewalResponse::new)
                .collect(Collectors.toList());
            return ResponseEntity.ok(renewals);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
