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

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Currency currency;
    @NotNull
    private BigDecimal unitPrice;

    public OrderItemEntity() {
    }

    public OrderItemEntity(ProductEntity product, Currency currency, BigDecimal unitPrice) {
        this.product = product;
        this.currency = currency;
        this.unitPrice = unitPrice;
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

    public ProductEntity getProduct() {
        return product;
    }

    public void setProduct(ProductEntity product) {
        this.product = product;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
}
