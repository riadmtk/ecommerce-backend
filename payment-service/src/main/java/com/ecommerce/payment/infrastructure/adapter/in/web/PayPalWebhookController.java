package com.ecommerce.payment.infrastructure.adapter.in.web;

import com.ecommerce.payment.domain.port.in.ConfirmPaymentUseCase;
import com.ecommerce.payment.infrastructure.adapter.out.provider.PayPalAdapter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/payments/paypal/webhook")
@RequiredArgsConstructor
public class PayPalWebhookController {

    private final ConfirmPaymentUseCase confirmPaymentUseCase;
    private final PayPalAdapter payPalAdapter;
    private final ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<String> handleWebhook(@RequestBody String payload) {
        try {
            JsonNode event = objectMapper.readTree(payload);
            String eventType = event.get("event_type").asText();
            log.info("Webhook PayPal reçu : {}", eventType);

            switch (eventType) {
                case "CHECKOUT.ORDER.APPROVED" -> {
                    String paypalOrderId = event.get("resource").get("id").asText();
                    // Capturer l'ordre puis confirmer
                    payPalAdapter.captureOrder(paypalOrderId);
                    confirmPaymentUseCase.confirm(
                            new ConfirmPaymentUseCase.ConfirmPaymentCommand(
                                    paypalOrderId, true, null
                            )
                    );
                }
                case "PAYMENT.CAPTURE.DENIED" -> {
                    String paypalOrderId = event.get("resource")
                            .get("supplementary_data")
                            .get("related_ids")
                            .get("order_id").asText();
                    confirmPaymentUseCase.confirm(
                            new ConfirmPaymentUseCase.ConfirmPaymentCommand(
                                    paypalOrderId, false, "Paiement PayPal refusé"
                            )
                    );
                }
                default -> log.debug("Event PayPal non traité : {}", eventType);
            }

            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            log.error("Erreur traitement webhook PayPal", e);
            return ResponseEntity.ok("OK"); // Toujours 200 pour éviter les retries PayPal
        }
    }
}