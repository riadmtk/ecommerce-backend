package com.ecommerce.user.domain.exception;

public class EmailNotVerifiedException extends RuntimeException {
    public EmailNotVerifiedException() {
        super("Veuillez vérifier votre adresse email avant de vous connecter.");
    }
}