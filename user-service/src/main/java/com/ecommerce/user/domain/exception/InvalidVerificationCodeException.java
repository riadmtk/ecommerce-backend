package com.ecommerce.user.domain.exception;

public class InvalidVerificationCodeException extends RuntimeException {
    public InvalidVerificationCodeException() {
        super("Code de vérification invalide ou expiré.");
    }

    public InvalidVerificationCodeException(String message) {
        super(message);
    }
}