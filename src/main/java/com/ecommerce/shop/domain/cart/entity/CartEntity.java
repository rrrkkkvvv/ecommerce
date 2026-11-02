package com.ecommerce.shop.domain.cart.entity;


import com.ecommerce.shop.domain.product.entity.ProductEntity;
import com.ecommerce.shop.domain.user.entity.UserEntity;
import jakarta.persistence.*;

import java.util.Set;

@Table(name = "cart")
@Entity
public class CartEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne()
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToMany()
    @JoinTable(
            name = "cart_product",
            joinColumns = { @JoinColumn(name = "cart_id") },
            inverseJoinColumns = { @JoinColumn(name = "product_id") }
    )
    private Set<ProductEntity> products;

    public CartEntity(){}

    public Long getId() {
        return id;
    }


    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public Set<ProductEntity> getProducts() {
        return products;
    }

    public void setProducts(Set<ProductEntity> products) {
        this.products = products;
    }
}
