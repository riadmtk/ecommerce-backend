package com.ecommerce.payment.domain.port.out;

import com.ecommerce.payment.domain.model.Payment;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepositoryPort {
    Payment save(Payment payment);
    Optional<Payment> findById(UUID id);
    Optional<Payment> findByTransactionId(String transactionId);
    Optional<Payment> findByOrderId(UUID orderId);
}