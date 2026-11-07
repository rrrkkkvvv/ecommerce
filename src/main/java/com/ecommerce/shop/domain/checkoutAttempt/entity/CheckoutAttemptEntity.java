package com.ecommerce.shop.domain.checkoutAttempt.entity;

import com.ecommerce.shop.domain.order.entity.OrderEntity;
import com.ecommerce.shop.domain.payment.entity.PaymentEntity;
import com.ecommerce.shop.domain.user.entity.UserEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="checkout_attempt")
public class CheckoutAttemptEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private OrderEntity order;

    @OneToOne
    @JoinColumn(name = "payment_id", referencedColumnName = "id")
    private PaymentEntity payment;

    @ManyToOne()
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    private CheckoutAttemptStatus status;

    private String sessionId;
    private LocalDateTime sessionExpiresAt;

    public LocalDateTime getSessionExpiresAt() {
        return sessionExpiresAt;
    }

    public void setSessionExpiresAt(LocalDateTime sessionExpiresAt) {
        this.sessionExpiresAt = sessionExpiresAt;
    }

    public CheckoutAttemptEntity(){}


    public Long getId() {
        return id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public OrderEntity getOrder() {
        return order;
    }

    public void setOrder(OrderEntity order) {
        this.order = order;
    }

    public PaymentEntity getPayment() {
        return payment;
    }

    public void setPayment(PaymentEntity payment) {
        this.payment = payment;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public CheckoutAttemptStatus getStatus() {
        return status;
    }

    public void setStatus(CheckoutAttemptStatus status) {
        this.status = status;
    }
}
