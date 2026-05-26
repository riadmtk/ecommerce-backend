package com.ecommerce.payment.infrastructure.adapter.in.web;

import com.ecommerce.payment.domain.port.in.ConfirmPaymentUseCase;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/payments/stripe/webhook")
@RequiredArgsConstructor
public class StripeWebhookController {

    private final ConfirmPaymentUseCase confirmPaymentUseCase;

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    @PostMapping
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            log.warn("Signature Stripe invalide");
            return ResponseEntity.badRequest().body("Signature invalide");
        }

        log.info("Webhook Stripe reçu : {}", event.getType());

        switch (event.getType()) {
            case "payment_intent.succeeded" -> {
                PaymentIntent intent = (PaymentIntent) event.getData().getObject();
                confirmPaymentUseCase.confirm(
                        new ConfirmPaymentUseCase.ConfirmPaymentCommand(
                                intent.getId(), true, null
                        )
                );
            }
            case "payment_intent.payment_failed" -> {
                PaymentIntent intent = (PaymentIntent) event.getData().getObject();
                String reason = intent.getLastPaymentError() != null
                        ? intent.getLastPaymentError().getMessage()
                        : "Paiement refusé";
                confirmPaymentUseCase.confirm(
                        new ConfirmPaymentUseCase.ConfirmPaymentCommand(
                                intent.getId(), false, reason
                        )
                );
            }
            default -> log.debug("Event Stripe non traité : {}", event.getType());
        }

        return ResponseEntity.ok("OK");
    }
}