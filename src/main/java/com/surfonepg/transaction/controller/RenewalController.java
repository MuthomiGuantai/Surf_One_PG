package com.surfonepg.transaction.controller;

import com.surfonepg.transaction.dto.RenewalResponse;
import com.surfonepg.transaction.entity.Renewal;
import com.surfonepg.transaction.service.RenewalService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/renewals")
public class RenewalController {

    private final RenewalService renewalService;

    public RenewalController(RenewalService renewalService) {
        this.renewalService = renewalService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<RenewalResponse> getRenewalById(@PathVariable Long id) {
        return renewalService.getRenewalById(id)
            .map(renewal -> ResponseEntity.ok(new RenewalResponse(renewal)))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/subscription/{subscriptionId}")
    public ResponseEntity<List<RenewalResponse>> getSubscriptionRenewals(@PathVariable Long subscriptionId) {
        try {
            List<RenewalResponse> renewals = renewalService.getSubscriptionRenewals(subscriptionId)
                .stream()
                .map(RenewalResponse::new)
                .collect(Collectors.toList());
            return ResponseEntity.ok(renewals);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RenewalResponse>> getUserRenewals(@PathVariable Long userId) {
        try {
            List<RenewalResponse> renewals = renewalService.getUserRenewals(userId)
                .stream()
                .map(RenewalResponse::new)
                .collect(Collectors.toList());
            return ResponseEntity.ok(renewals);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}/latest")
    public ResponseEntity<RenewalResponse> getUserLatestRenewal(@PathVariable Long userId) {
        try {
            List<Renewal> userRenewals = renewalService.getUserRenewals(userId);
            if (userRenewals.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(new RenewalResponse(userRenewals.get(0)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/subscription/{subscriptionId}/latest")
    public ResponseEntity<RenewalResponse> getSubscriptionLatestRenewal(@PathVariable Long subscriptionId) {
        try {
            return renewalService.getLatestRenewal(subscriptionId)
                .map(renewal -> ResponseEntity.ok(new RenewalResponse(renewal)))
                .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<RenewalResponse>> getAllRenewals() {
        List<RenewalResponse> renewals = renewalService.getAllRenewals()
            .stream()
            .map(RenewalResponse::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(renewals);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<RenewalResponse>> getRenewalsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        List<RenewalResponse> renewals = renewalService.getRenewalsByDateRange(start, end)
            .stream()
            .map(RenewalResponse::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(renewals);
    }

    @GetMapping("/user/{userId}/stats")
    public ResponseEntity<RenewalStatsResponse> getUserRenewalStats(@PathVariable Long userId) {
        try {
            RenewalService.RenewalStats stats = renewalService.getUserRenewalStats(userId);
            return ResponseEntity.ok(new RenewalStatsResponse(stats.getTotalRenewals(), stats.getTotalSpent()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/subscription/{subscriptionId}/previous-renewals")
    public ResponseEntity<List<RenewalResponse>> getPreviousSubscriptionRenewals(@PathVariable Long subscriptionId) {
        try {
            List<RenewalResponse> renewals = renewalService.getPreviousSubscriptionRenewals(subscriptionId)
                .stream()
                .map(RenewalResponse::new)
                .collect(Collectors.toList());
            return ResponseEntity.ok(renewals);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    public static class RenewalStatsResponse {
        private int totalRenewals;
        private java.math.BigDecimal totalSpent;

        public RenewalStatsResponse(int totalRenewals, java.math.BigDecimal totalSpent) {
            this.totalRenewals = totalRenewals;
            this.totalSpent = totalSpent;
        }

        public int getTotalRenewals() { return totalRenewals; }
        public java.math.BigDecimal getTotalSpent() { return totalSpent; }
    }
}

