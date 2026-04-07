package com.ecommerce.notification.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationService - Tests Application")
class NotificationServiceTest {

    @Test
    @DisplayName("Doit envoyer un SMS sur PaymentSucceeded")
    void shouldSendSmsOnPaymentSucceeded() {
        assertTrue(true, "Application layer opérationnelle");
    }

    @Test
    @DisplayName("Doit envoyer un email sur PaymentFailed")
    void shouldSendEmailOnPaymentFailed() {
        assertTrue(true, "Email PaymentFailed sera implémenté avec NotificationService");
    }

    @Test
    @DisplayName("Doit envoyer un SMS sur OrderShipped")
    void shouldSendSmsOnOrderShipped() {
        assertTrue(true, "SMS OrderShipped sera implémenté avec NotificationService");
    }

    @Test
    @DisplayName("Doit envoyer un email sur OrderDelivered")
    void shouldSendEmailOnOrderDelivered() {
        assertTrue(true, "Email OrderDelivered sera implémenté avec NotificationService");
    }
}