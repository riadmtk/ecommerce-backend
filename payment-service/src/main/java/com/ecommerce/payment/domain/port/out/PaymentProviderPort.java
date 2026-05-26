package com.ecommerce.payment.domain.port.out;

import com.ecommerce.payment.domain.model.Payment;
import com.ecommerce.payment.domain.model.PaymentMethod;

public interface PaymentProviderPort {
    record PaymentIntentResult(String transactionId, String clientSecret) {}
    PaymentIntentResult createPaymentIntent(Payment payment);
    void refund(Payment payment);
    boolean supports(PaymentMethod method);
}