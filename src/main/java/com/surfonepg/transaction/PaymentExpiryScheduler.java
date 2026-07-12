package com.surfonepg.transaction;

import com.surfonepg.config.SurfOnePgProperties;
import com.surfonepg.transaction.entity.PaymentTransaction;
import com.surfonepg.transaction.repository.PaymentTransactionRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class PaymentExpiryScheduler {

    private final PaymentTransactionRepository transactionRepository;
    private final SurfOnePgProperties properties;

    public PaymentExpiryScheduler(PaymentTransactionRepository transactionRepository, SurfOnePgProperties properties) {
        this.transactionRepository = transactionRepository;
        this.properties = properties;
    }

    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void expireStalePayments() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(properties.getPaymentTimeoutMinutes());

        List<PaymentTransaction> stale = transactionRepository.findAll().stream()
                .filter(t -> t.getStatus() == PaymentTransaction.Status.PENDING)
                .filter(t -> t.getCreatedAt().isBefore(cutoff))
                .toList();

        stale.forEach(t -> t.setStatus(PaymentTransaction.Status.EXPIRED));
        transactionRepository.saveAll(stale);
    }
}
