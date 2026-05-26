package com.ecommerce.payment.domain.port.out;

import com.ecommerce.payment.domain.model.Payment;

public interface PaymentEventPublisherPort {
    void publishPaymentSucceeded(Payment payment);
    void publishPaymentFailed(Payment payment);
    void publishPaymentRefunded(Payment payment);
    public void publishPaymentCompleted(Payment payment);
}