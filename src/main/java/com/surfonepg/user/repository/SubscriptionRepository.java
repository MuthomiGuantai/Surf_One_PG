package com.surfonepg.user.repository;

import com.surfonepg.user.entity.Subscription;
import com.surfonepg.user.entity.User;
import com.surfonepg.packages.entity.DataPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByUser(User user);
    List<Subscription> findByUserAndStatusOrderByCreatedAtDesc(User user, Subscription.Status status);
    List<Subscription> findByDataPackage(DataPackage dataPackage);
    List<Subscription> findByStatus(Subscription.Status status);
    List<Subscription> findByStatusAndExpiresAtBefore(Subscription.Status status, LocalDateTime dateTime);
    Optional<Subscription> findByIdAndUser(Long subscriptionId, User user);
}

