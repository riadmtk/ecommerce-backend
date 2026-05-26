package com.ecommerce.payment.domain.exception;

public class PaymentAlreadyProcessedException extends RuntimeException {
    public PaymentAlreadyProcessedException(String transactionId) {
        super("Paiement déjà traité pour la transaction : " + transactionId);
    }
}