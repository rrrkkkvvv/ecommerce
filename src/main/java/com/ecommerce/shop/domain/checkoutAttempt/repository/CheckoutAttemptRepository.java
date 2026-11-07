package com.ecommerce.shop.domain.checkoutAttempt.repository;

import com.ecommerce.shop.domain.checkoutAttempt.entity.CheckoutAttemptEntity;
 import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CheckoutAttemptRepository extends JpaRepository<CheckoutAttemptEntity, Long> {

    Optional<CheckoutAttemptEntity> findByOrderId(Long orderId);


    @Modifying
    @Query(value = """
            UPDATE checkout_attempt SET status = 'PAYMENT_PENDING' WHERE id = :id and status = 'ACTIVE'
            """, nativeQuery = true)
    int makeCheckoutPending(Long id);
}
