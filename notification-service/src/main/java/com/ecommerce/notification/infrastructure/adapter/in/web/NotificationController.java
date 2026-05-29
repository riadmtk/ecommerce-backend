package com.ecommerce.notification.infrastructure.adapter.in.web;

import com.ecommerce.notification.domain.port.in.SendOrderConfirmationUseCase;
import com.ecommerce.notification.infrastructure.adapter.in.web.dto.NotificationRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    // On injecte l'interface pure du Use Case
    private final SendOrderConfirmationUseCase sendOrderConfirmationUseCase;

    @PostMapping("/send-confirmation")
    public ResponseEntity<String> sendManualConfirmation(@RequestBody @Valid NotificationRequest request) {
        // 🎯 Mise à jour du log pour refléter le changement sémantique
        log.info("🌐 Received REST request to send confirmation for Reference ID: {}", request.referenceId());

        // 🎯 On utilise referenceId() au lieu de orderId()
        sendOrderConfirmationUseCase.sendConfirmation(
                request.referenceId(),
                request.email(),
                request.phoneNumber()
        );

        return ResponseEntity.ok("Notification process triggered successfully.");
    }
}