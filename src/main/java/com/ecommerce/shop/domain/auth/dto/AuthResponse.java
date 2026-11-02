package com.ecommerce.shop.domain.auth.dto;

import com.ecommerce.shop.domain.user.dto.User;

public record AuthResponse(User user, String token) {
}
