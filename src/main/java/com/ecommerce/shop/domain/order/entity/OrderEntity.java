package com.ecommerce.shop.domain.order.entity;

import com.ecommerce.shop.domain.payment.entity.PaymentEntity;
import com.ecommerce.shop.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

@Table(name="orders")
@Entity
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @NotNull
    private BigDecimal totalPrice;

    @NotNull
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @OneToMany(
            mappedBy = "order",
            cascade =  CascadeType.ALL,
            orphanRemoval = true
    )
    private List<OrderItemEntity> items;



    public OrderEntity(){}
    public OrderEntity(UserEntity user, BigDecimal totalPrice, OrderStatus status, Currency currency, List<OrderItemEntity> items) {
        this.user = user;
        this.totalPrice = totalPrice;
        this.status = status;
        this.currency = currency;
        this.items = items;
        for(OrderItemEntity orderItem: items){
            orderItem.setOrder(this);
        }
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

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public List<OrderItemEntity> getItems() {
        return items;
    }

    public void setItems(List<OrderItemEntity> items) {
        this.items = items;
    }


}
