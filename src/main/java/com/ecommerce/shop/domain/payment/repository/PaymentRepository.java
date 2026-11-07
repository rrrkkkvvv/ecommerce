package com.ecommerce.shop.domain.payment.repository;

import com.ecommerce.shop.domain.payment.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
    Optional< PaymentEntity> findByProviderPaymentId(String providerPaymentId);
    @Modifying
    @Query(value = """
          UPDATE payment
          SET status = 'SUCCEEDED'
          WHERE status = 'PROCESSING'
          AND provider_payment_id = :providerPaymentId
            """, nativeQuery = true)
        int makePaymentSucceeded(String providerPaymentId);
}
