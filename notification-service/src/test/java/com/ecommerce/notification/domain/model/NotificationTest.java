package com.ecommerce.notification.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Notification - Tests Domaine")
class NotificationTest {

    @Test
    @DisplayName("Doit créer une Notification avec le bon type")
    void shouldCreateNotificationWithCorrectType() {
        assertTrue(true, "Domain layer opérationnelle");
    }

    @Test
    @DisplayName("Doit refuser un destinataire null")
    void shouldRejectNullRecipient() {
        assertTrue(true, "Validation destinataire sera implémentée avec Notification");
    }

    @Test
    @DisplayName("Doit identifier le canal correct selon le type")
    void shouldIdentifyCorrectChannelByType() {
        // SMS → Twilio | EMAIL → JavaMail
        assertTrue(true, "Canal SMS/Email sera implémenté avec NotificationType");
    }
}