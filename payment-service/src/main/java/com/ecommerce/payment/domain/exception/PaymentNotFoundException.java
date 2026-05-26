package com.ecommerce.payment.domain.exception;

import java.util.UUID;

public class PaymentNotFoundException extends RuntimeException {
    public PaymentNotFoundException(UUID id) {
        super("Paiement introuvable avec l'id : " + id);
    }
    public PaymentNotFoundException(String field, String value) {
        super("Paiement introuvable avec " + field + " : " + value);
    }
}