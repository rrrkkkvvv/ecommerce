package com.ecommerce.shop.domain.user.dto;

import jakarta.validation.constraints.NotNull;

public record User (
        @NotNull
        Long id,
        @NotNull
        String email,
        @NotNull
        String name
){
}
