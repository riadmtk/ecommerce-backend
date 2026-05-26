package com.ecommerce.payment.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Payment - Tests Domaine")
class PaymentTest {

    @Test
    @DisplayName("Doit créer un Payment en statut PENDING")
    void shouldCreatePaymentWithPendingStatus() {
        Payment payment = Payment.create(
                UUID.randomUUID(), UUID.randomUUID(),
                BigDecimal.valueOf(49.99), "EUR", PaymentMethod.STRIPE);

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(payment.getId()).isNotNull();
        assertThat(payment.isPending()).isTrue();
    }

    @Test
    @DisplayName("Doit passer en SUCCESS")
    void shouldSucceed() {
        Payment payment = Payment.create(UUID.randomUUID(), UUID.randomUUID(),
                BigDecimal.valueOf(49.99), "EUR", PaymentMethod.STRIPE);
        Payment succeeded = payment.succeed("pi_test_123");
        assertThat(succeeded.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(succeeded.isSuccess()).isTrue();
    }

    @Test
    @DisplayName("Doit passer en FAILED")
    void shouldFail() {
        Payment payment = Payment.create(UUID.randomUUID(), UUID.randomUUID(),
                BigDecimal.valueOf(49.99), "EUR", PaymentMethod.STRIPE);
        Payment failed = payment.fail("Carte refusée");
        assertThat(failed.getStatus()).isEqualTo(PaymentStatus.FAILED);
        assertThat(failed.getFailureReason()).isEqualTo("Carte refusée");
    }

    @Test
    @DisplayName("Doit passer en REFUNDED depuis SUCCESS")
    void shouldRefund() {
        Payment payment = Payment.create(UUID.randomUUID(), UUID.randomUUID(),
                BigDecimal.valueOf(49.99), "EUR", PaymentMethod.STRIPE);
        Payment succeeded = payment.succeed("pi_test_123");
        Payment refunded = succeeded.refund();
        assertThat(refunded.getStatus()).isEqualTo(PaymentStatus.REFUNDED);
    }
}