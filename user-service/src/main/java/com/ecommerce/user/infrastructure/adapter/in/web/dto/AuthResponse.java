package com.ecommerce.user.infrastructure.adapter.in.web.dto;

public record AuthResponse(
        String token,
        String tokenType,
        long expiresIn,
        UserResponse user
) {}