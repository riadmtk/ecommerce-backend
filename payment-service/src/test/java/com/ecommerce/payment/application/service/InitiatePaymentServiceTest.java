package com.ecommerce.payment.application.service;

import com.ecommerce.payment.domain.model.Payment;
import com.ecommerce.payment.domain.model.PaymentMethod;
import com.ecommerce.payment.domain.port.in.InitiatePaymentUseCase;
import com.ecommerce.payment.domain.port.out.PaymentProviderPort;
import com.ecommerce.payment.domain.port.out.PaymentRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("InitiatePaymentService - Tests Application")
class InitiatePaymentServiceTest {

    @Mock private PaymentRepositoryPort paymentRepository;
    @Mock private PaymentProviderPort stripeProvider;

    private InitiatePaymentService service;

    @BeforeEach
    void setUp() {
        when(stripeProvider.supports(PaymentMethod.STRIPE)).thenReturn(true);
        service = new InitiatePaymentService(paymentRepository, List.of(stripeProvider));
    }

    @Test
    @DisplayName("Doit initier un paiement Stripe avec succès")
    void shouldInitiateStripePayment() {
        when(stripeProvider.createPaymentIntent(any())).thenReturn(
                new PaymentProviderPort.PaymentIntentResult("pi_test_123", "pi_secret_123"));
        when(paymentRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Payment result = service.initiate(new InitiatePaymentUseCase.InitiatePaymentCommand(
                UUID.randomUUID(), UUID.randomUUID(),
                BigDecimal.valueOf(49.99), "EUR", PaymentMethod.STRIPE));

        assertThat(result).isNotNull();
        assertThat(result.getClientSecret()).isEqualTo("pi_secret_123");
        verify(paymentRepository).save(any());
    }

    @Test
    @DisplayName("Doit lever une exception si provider non supporté")
    void shouldThrowWhenProviderNotSupported() {
        when(stripeProvider.supports(PaymentMethod.PAYPAL)).thenReturn(false);

        assertThatThrownBy(() -> service.initiate(
                new InitiatePaymentUseCase.InitiatePaymentCommand(
                        UUID.randomUUID(), UUID.randomUUID(),
                        BigDecimal.valueOf(49.99), "EUR", PaymentMethod.PAYPAL)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}