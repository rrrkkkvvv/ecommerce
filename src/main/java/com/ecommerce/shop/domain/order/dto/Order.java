package com.ecommerce.shop.domain.order.dto;

import com.ecommerce.shop.domain.order.entity.Currency;
import com.ecommerce.shop.domain.order.entity.OrderStatus;


import java.math.BigDecimal;
import java.util.List;

public record Order(
        Long id,
        Long userId,
        BigDecimal totalPrice,
        OrderStatus status,
        Currency currency,
        List<OrderItem> orderItems
) {
}
