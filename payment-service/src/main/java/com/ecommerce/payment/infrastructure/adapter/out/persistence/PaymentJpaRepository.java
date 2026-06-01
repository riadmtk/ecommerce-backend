package com.ecommerce.payment.infrastructure.adapter.out.persistence;

import com.ecommerce.payment.infrastructure.adapter.out.persistence.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentJpaRepository extends JpaRepository<PaymentEntity, UUID> {
    Optional<PaymentEntity> findByTransactionId(String transactionId);
    Optional<PaymentEntity> findByOrderId(UUID orderId);
}