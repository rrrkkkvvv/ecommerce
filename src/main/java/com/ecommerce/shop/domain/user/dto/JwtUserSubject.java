package com.ecommerce.shop.domain.user.dto;

import com.ecommerce.shop.domain.user.entity.Roles;

import java.util.Set;

public record JwtUserSubject (
        Long id,
        String email,
        Set<Roles> roles
) {
}
