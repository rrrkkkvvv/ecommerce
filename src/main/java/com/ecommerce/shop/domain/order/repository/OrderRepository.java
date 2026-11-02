package com.ecommerce.shop.domain.order.repository;

import com.ecommerce.shop.domain.order.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
}
