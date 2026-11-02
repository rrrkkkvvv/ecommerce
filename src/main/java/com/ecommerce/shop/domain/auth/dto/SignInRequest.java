package com.ecommerce.shop.domain.auth.dto;

import com.ecommerce.shop.util.PatternConstants;
import jakarta.validation.constraints.Pattern;


public record SignInRequest(
        @Pattern(regexp = PatternConstants.EMAIL)
        String email,
        String password
) {
}
