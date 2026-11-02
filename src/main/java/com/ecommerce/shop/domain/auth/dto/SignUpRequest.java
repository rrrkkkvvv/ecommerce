package com.ecommerce.shop.domain.auth.dto;

import com.ecommerce.shop.util.PatternConstants;
import jakarta.validation.constraints.Pattern;

public record SignUpRequest(
        @Pattern(regexp = PatternConstants.EMAIL)
        String email,
        String name,
        String password
) {
}
