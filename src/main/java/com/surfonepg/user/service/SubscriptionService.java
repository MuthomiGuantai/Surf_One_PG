package com.surfonepg.user.service;

import com.surfonepg.user.entity.Subscription;
import com.surfonepg.user.entity.User;
import com.surfonepg.user.dto.CreateSubscriptionRequest;
import com.surfonepg.user.repository.SubscriptionRepository;
import com.surfonepg.user.repository.UserRepository;
import com.surfonepg.packages.entity.DataPackage;
import com.surfonepg.packages.repository.DataPackageRepository;
import com.surfonepg.transaction.entity.PaymentTransaction;
import com.surfonepg.transaction.entity.Renewal;
import com.surfonepg.transaction.repository.PaymentTransactionRepository;
import com.surfonepg.transaction.service.RenewalService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final DataPackageRepository dataPackageRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final RenewalService renewalService;

    public SubscriptionService(
            SubscriptionRepository subscriptionRepository,
            UserRepository userRepository,
            DataPackageRepository dataPackageRepository,
            PaymentTransactionRepository paymentTransactionRepository,
            RenewalService renewalService
    ) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
        this.dataPackageRepository = dataPackageRepository;
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.renewalService = renewalService;
    }

    @Transactional
    public Subscription createSubscription(CreateSubscriptionRequest request) {
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + request.getUserId()));

        DataPackage dataPackage = dataPackageRepository.findById(request.getPackageId())
            .orElseThrow(() -> new IllegalArgumentException("Package not found with id: " + request.getPackageId()));

        Subscription subscription = new Subscription(user, dataPackage);

        if (request.getPaymentTransactionId() != null) {
            PaymentTransaction paymentTransaction = paymentTransactionRepository.findById(request.getPaymentTransactionId())
                .orElseThrow(() -> new IllegalArgumentException("Payment transaction not found with id: " + request.getPaymentTransactionId()));
            subscription.setPaymentTransaction(paymentTransaction);
        }

        return subscriptionRepository.save(subscription);
    }

    @Transactional(readOnly = true)
    public Optional<Subscription> getSubscriptionById(Long id) {
        return subscriptionRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Subscription> getUserSubscriptions(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        return subscriptionRepository.findByUser(user);
    }

    @Transactional(readOnly = true)
    public List<Subscription> getUserActiveSubscriptions(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        return subscriptionRepository.findByUserAndStatusOrderByCreatedAtDesc(user, Subscription.Status.ACTIVE);
    }

    @Transactional(readOnly = true)
    public List<Subscription> getSubscriptionsByPackage(Long packageId) {
        DataPackage dataPackage = dataPackageRepository.findById(packageId)
            .orElseThrow(() -> new IllegalArgumentException("Package not found with id: " + packageId));
        return subscriptionRepository.findByDataPackage(dataPackage);
    }

    @Transactional(readOnly = true)
    public List<Subscription> getSubscriptionsByStatus(Subscription.Status status) {
        return subscriptionRepository.findByStatus(status);
    }

    @Transactional
    public Subscription activateSubscription(Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
            .orElseThrow(() -> new IllegalArgumentException("Subscription not found with id: " + subscriptionId));

        subscription.setStatus(Subscription.Status.ACTIVE);
        subscription.setActivatedAt(LocalDateTime.now());

        // Calculate expiration time based on package duration
        Integer durationMinutes = subscription.getDataPackage().getDurationMinutes();
        subscription.setExpiresAt(LocalDateTime.now().plusMinutes(durationMinutes));

        return subscriptionRepository.save(subscription);
    }

    @Transactional
    public Subscription expireSubscription(Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
            .orElseThrow(() -> new IllegalArgumentException("Subscription not found with id: " + subscriptionId));

        subscription.setStatus(Subscription.Status.EXPIRED);
        return subscriptionRepository.save(subscription);
    }

    @Transactional
    public Subscription cancelSubscription(Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
            .orElseThrow(() -> new IllegalArgumentException("Subscription not found with id: " + subscriptionId));

        subscription.setStatus(Subscription.Status.CANCELLED);
        return subscriptionRepository.save(subscription);
    }

    @Transactional
    public void expireExpiredSubscriptions() {
        List<Subscription> expiredSubscriptions = subscriptionRepository
            .findByStatusAndExpiresAtBefore(Subscription.Status.ACTIVE, LocalDateTime.now());

        for (Subscription subscription : expiredSubscriptions) {
            subscription.setStatus(Subscription.Status.EXPIRED);
        }

        subscriptionRepository.saveAll(expiredSubscriptions);
    }

    @Transactional(readOnly = true)
    public List<Subscription> getAllSubscriptions() {
        return subscriptionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public boolean hasActiveSubscription(Long userId, Long packageId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        List<Subscription> activeSubscriptions = subscriptionRepository
            .findByUserAndStatusOrderByCreatedAtDesc(user, Subscription.Status.ACTIVE);

        return activeSubscriptions.stream()
            .anyMatch(sub -> sub.getDataPackage().getId().equals(packageId));
    }

    /**
     * Renew an expired subscription with a new payment
     * Expires the old subscription and creates a new active one
     */
    @Transactional
    public Subscription renewSubscription(Long expiredSubscriptionId, PaymentTransaction paymentTransaction) {
        Subscription expiredSubscription = subscriptionRepository.findById(expiredSubscriptionId)
            .orElseThrow(() -> new IllegalArgumentException("Subscription not found with id: " + expiredSubscriptionId));

        if (!expiredSubscription.getStatus().equals(Subscription.Status.EXPIRED)) {
            throw new IllegalArgumentException("Can only renew expired subscriptions");
        }

        User user = expiredSubscription.getUser();
        DataPackage dataPackage = expiredSubscription.getDataPackage();

        // Create new subscription
        Subscription newSubscription = new Subscription(user, dataPackage);
        newSubscription.setPaymentTransaction(paymentTransaction);
        Subscription savedSubscription = subscriptionRepository.save(newSubscription);

        // Record the renewal
        renewalService.recordRenewal(savedSubscription, expiredSubscription, paymentTransaction);

        return savedSubscription;
    }

    /**
     * Renew an active subscription (upgrade/extend)
     * Keeps the old subscription but marks it as providing extra service
     */
    @Transactional
    public Subscription renewSubscriptionWithoutExpiring(Long subscriptionId, PaymentTransaction paymentTransaction) {
        Subscription oldSubscription = subscriptionRepository.findById(subscriptionId)
            .orElseThrow(() -> new IllegalArgumentException("Subscription not found with id: " + subscriptionId));

        User user = oldSubscription.getUser();
        DataPackage dataPackage = oldSubscription.getDataPackage();

        // Create new subscription
        Subscription newSubscription = new Subscription(user, dataPackage);
        newSubscription.setPaymentTransaction(paymentTransaction);
        Subscription savedSubscription = subscriptionRepository.save(newSubscription);

        // Record the renewal
        renewalService.recordRenewal(savedSubscription, oldSubscription, paymentTransaction);

        return savedSubscription;
    }

    /**
     * Get all renewals for a subscription
     */
    @Transactional(readOnly = true)
    public List<Renewal> getSubscriptionRenewals(Long subscriptionId) {
        return renewalService.getSubscriptionRenewals(subscriptionId);
    }

    /**
     * Get all renewals for a user
     */
    @Transactional(readOnly = true)
    public List<Renewal> getUserRenewals(Long userId) {
        return renewalService.getUserRenewals(userId);
    }
}




