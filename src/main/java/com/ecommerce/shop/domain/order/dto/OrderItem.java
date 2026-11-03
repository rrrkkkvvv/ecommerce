package com.ecommerce.shop.domain.order.dto;

import com.ecommerce.shop.domain.order.entity.Currency;
import com.ecommerce.shop.domain.product.entity.ProductEntity;


import java.math.BigDecimal;

public record OrderItem(
    Long id,
    Long productId,
    String productName,
    Currency currency,
    BigDecimal unitPrice

) {
}
