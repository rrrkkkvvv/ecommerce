package com.ecommerce.shop.domain.cart.repository;

import com.ecommerce.shop.domain.cart.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<CartEntity, Long> {
}
