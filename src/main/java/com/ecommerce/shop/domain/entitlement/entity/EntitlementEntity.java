package com.ecommerce.shop.domain.entitlement.entity;

import com.ecommerce.shop.domain.order.entity.OrderEntity;
import com.ecommerce.shop.domain.product.entity.ProductEntity;
import com.ecommerce.shop.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Table(name="entitlement")
@Entity
public class EntitlementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;
    @NotNull
    private LocalDateTime grantedAt;

    @NotNull
    private LocalDateTime  expiresAt;
    public EntitlementEntity(){}

    public EntitlementEntity(UserEntity user, ProductEntity product, OrderEntity order, LocalDateTime grantedAt, LocalDateTime expiresAt) {
        this.user = user;
        this.product = product;
        this.order = order;
        this.grantedAt = grantedAt;
        this.expiresAt = expiresAt;
    }

    public Long getId() {
        return id;
    }


    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public ProductEntity getProduct() {
        return product;
    }

    public void setProduct(ProductEntity product) {
        this.product = product;
    }

    public OrderEntity getOrder() {
        return order;
    }

    public void setOrder(OrderEntity order) {
        this.order = order;
    }

    public LocalDateTime getGrantedAt() {
        return grantedAt;
    }

    public void setGrantedAt(LocalDateTime grantedAt) {
        this.grantedAt = grantedAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}
