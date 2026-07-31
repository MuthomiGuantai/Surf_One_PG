package com.surfonepg.transaction.service;

import com.surfonepg.transaction.entity.PaymentTransaction;
import com.surfonepg.transaction.entity.Renewal;
import com.surfonepg.transaction.repository.PaymentTransactionRepository;
import com.surfonepg.transaction.repository.RenewalRepository;
import com.surfonepg.user.entity.Subscription;
import com.surfonepg.user.entity.User;
import com.surfonepg.user.repository.SubscriptionRepository;
import com.surfonepg.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RenewalService {

    private final RenewalRepository renewalRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;

    public RenewalService(
            RenewalRepository renewalRepository,
            SubscriptionRepository subscriptionRepository,
            UserRepository userRepository,
            PaymentTransactionRepository paymentTransactionRepository
    ) {
        this.renewalRepository = renewalRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
        this.paymentTransactionRepository = paymentTransactionRepository;
    }

    /**
     * Create a renewal record when subscription is renewed
     */
    @Transactional
    public Renewal recordRenewal(Subscription newSubscription, Subscription previousSubscription, PaymentTransaction paymentTransaction) {
        Renewal renewal = new Renewal(newSubscription, previousSubscription, paymentTransaction);
        paymentTransaction.setRenewal(true);
        paymentTransaction.setSubscription(newSubscription);
        paymentTransactionRepository.save(paymentTransaction);
        return renewalRepository.save(renewal);
    }

    /**
     * Create a renewal record (first-time renewal without previous subscription)
     */
    @Transactional
    public Renewal recordRenewal(Subscription subscription, PaymentTransaction paymentTransaction) {
        Renewal renewal = new Renewal(subscription, paymentTransaction);
        paymentTransaction.setRenewal(true);
        paymentTransaction.setSubscription(subscription);
        paymentTransactionRepository.save(paymentTransaction);
        return renewalRepository.save(renewal);
    }

    @Transactional(readOnly = true)
    public Optional<Renewal> getRenewalById(Long id) {
        return renewalRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Renewal> getSubscriptionRenewals(Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
            .orElseThrow(() -> new IllegalArgumentException("Subscription not found with id: " + subscriptionId));
        return renewalRepository.findBySubscription(subscription);
    }

    @Transactional(readOnly = true)
    public List<Renewal> getUserRenewals(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        return renewalRepository.findBySubscriptionUserOrderByRenewedAtDesc(user);
    }

    @Transactional(readOnly = true)
    public List<Renewal> getRenewalsByDateRange(LocalDateTime start, LocalDateTime end) {
        return renewalRepository.findByRenewedAtBetween(start, end);
    }

    @Transactional(readOnly = true)
    public Optional<Renewal> getLatestRenewal(Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
            .orElseThrow(() -> new IllegalArgumentException("Subscription not found with id: " + subscriptionId));
        return renewalRepository.findFirstBySubscriptionOrderByRenewedAtDesc(subscription);
    }

    @Transactional(readOnly = true)
    public List<Renewal> getAllRenewals() {
        return renewalRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Renewal> getPreviousSubscriptionRenewals(Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
            .orElseThrow(() -> new IllegalArgumentException("Subscription not found with id: " + subscriptionId));
        return renewalRepository.findByPreviousSubscription(subscription);
    }

    /**
     * Get renewal statistics for a user
     */
    @Transactional(readOnly = true)
    public RenewalStats getUserRenewalStats(Long userId) {
        List<Renewal> renewals = getUserRenewals(userId);
        return new RenewalStats(
            renewals.size(),
            renewals.stream().map(r -> r.getPaymentTransaction().getAmountKes()).reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add)
        );
    }

    public static class RenewalStats {
        private int totalRenewals;
        private java.math.BigDecimal totalSpent;

        public RenewalStats(int totalRenewals, java.math.BigDecimal totalSpent) {
            this.totalRenewals = totalRenewals;
            this.totalSpent = totalSpent;
        }

        public int getTotalRenewals() { return totalRenewals; }
        public java.math.BigDecimal getTotalSpent() { return totalSpent; }
    }
}

