package com.ecommerce.user.domain.exception;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(UUID id) {
        super("Utilisateur introuvable avec l'id : " + id);
    }
    public UserNotFoundException(String email) {
        super("Utilisateur introuvable avec l'email : " + email);
    }
}