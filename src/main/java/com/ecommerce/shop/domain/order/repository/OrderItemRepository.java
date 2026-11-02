package com.ecommerce.shop.domain.order.repository;

import com.ecommerce.shop.domain.order.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {
}
