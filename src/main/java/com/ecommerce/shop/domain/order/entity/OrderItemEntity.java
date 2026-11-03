package com.ecommerce.shop.domain.order.entity;

import com.ecommerce.shop.domain.product.entity.ProductEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Table(name="order_item")
@Entity
public class OrderItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;
    @NotNull
    private Long productId;
    @NotNull
    private String productName;
    @NotNull
    private BigDecimal unitPrice;


    @NotNull
    @Enumerated(EnumType.STRING)
    private Currency currency;

    public OrderItemEntity() {
    }

    public OrderItemEntity( Long productId, String productName, BigDecimal unitPrice, Currency currency) {
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
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

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}
