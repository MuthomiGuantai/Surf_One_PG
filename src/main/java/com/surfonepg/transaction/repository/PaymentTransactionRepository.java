package com.surfonepg.transaction.repository;

import com.surfonepg.transaction.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    Optional<PaymentTransaction> findByMerchantReference(String merchantReference);
}
