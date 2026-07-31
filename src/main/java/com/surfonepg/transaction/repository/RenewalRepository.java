package com.surfonepg.transaction.repository;

import com.surfonepg.transaction.entity.Renewal;
import com.surfonepg.user.entity.Subscription;
import com.surfonepg.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RenewalRepository extends JpaRepository<Renewal, Long> {
    List<Renewal> findBySubscription(Subscription subscription);
    List<Renewal> findBySubscriptionUserOrderByRenewedAtDesc(User user);
    List<Renewal> findByRenewedAtBetween(LocalDateTime start, LocalDateTime end);
    Optional<Renewal> findFirstBySubscriptionOrderByRenewedAtDesc(Subscription subscription);
    List<Renewal> findByPreviousSubscription(Subscription previousSubscription);
}

