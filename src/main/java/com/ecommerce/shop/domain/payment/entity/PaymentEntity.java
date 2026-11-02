package com.ecommerce.shop.domain.payment.entity;

import com.ecommerce.shop.domain.order.entity.Currency;
import com.ecommerce.shop.domain.order.entity.OrderEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Table(name="payment")
@Entity
public class PaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @OneToOne(fetch = FetchType.LAZY, mappedBy = "payment")
    private OrderEntity order;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @NotNull
    private BigDecimal price;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Currency currency;

//    @NotNull
//    private String provider;

    private String providerPaymentId;

    public PaymentEntity(){}



    public PaymentEntity(PaymentStatus paymentStatus, BigDecimal totalPrice, Currency currency) {
        this.status = paymentStatus;
        this.price = totalPrice;
        this.currency = currency;
    }

    public Long getId() {
        return id;
    }


    public OrderEntity getOrder() {
        return order;
    }

    public void setOrder(OrderEntity order) {
        this.order = order;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public String getProviderPaymentId() {
        return providerPaymentId;
    }

    public void setProviderPaymentId(String providerPaymentId) {
        this.providerPaymentId = providerPaymentId;
    }
}
