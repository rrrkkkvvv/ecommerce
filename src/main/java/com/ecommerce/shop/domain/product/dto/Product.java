package com.ecommerce.shop.domain.product.dto;

import jakarta.validation.constraints.NotNull;

public record Product(
        @NotNull
        Long id,
        @NotNull
        String name,
        @NotNull
        Long price,
        @NotNull
        String description
) {
}
