package com.ecommerce.user.domain.port.out;

import com.ecommerce.user.domain.model.User;

public interface JwtPort {
    String generateToken(User user);
    String extractEmail(String token);
    boolean isTokenValid(String token, String email);
    long getExpirationInSeconds();
}