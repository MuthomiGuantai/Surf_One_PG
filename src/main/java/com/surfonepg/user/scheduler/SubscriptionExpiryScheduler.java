package com.surfonepg.user.scheduler;

import com.surfonepg.user.service.SubscriptionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class SubscriptionExpiryScheduler {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionExpiryScheduler.class);

    private final SubscriptionService subscriptionService;

    public SubscriptionExpiryScheduler(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    /**
     * Runs every 5 minutes to check and expire subscriptions that have passed their expiration time
     */
    @Scheduled(fixedRate = 300000, initialDelay = 60000)  // 5 minutes, 1 minute initial delay
    public void expireSubscriptions() {
        try {
            logger.info("Starting subscription expiry check");
            subscriptionService.expireExpiredSubscriptions();
            logger.info("Subscription expiry check completed successfully");
        } catch (Exception e) {
            logger.error("Error during subscription expiry check", e);
        }
    }
}

