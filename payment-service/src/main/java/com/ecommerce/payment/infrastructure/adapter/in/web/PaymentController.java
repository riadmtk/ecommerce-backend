package com.ecommerce.payment.infrastructure.adapter.in.web;

import com.ecommerce.payment.domain.model.Payment;
import com.ecommerce.payment.domain.port.in.GetPaymentUseCase;
import com.ecommerce.payment.domain.port.in.InitiatePaymentUseCase;
import com.ecommerce.payment.domain.port.in.RefundPaymentUseCase;
import com.ecommerce.payment.infrastructure.adapter.in.web.dto.InitiatePaymentRequest;
import com.ecommerce.payment.infrastructure.adapter.in.web.dto.PaymentCreatedResponse;
import com.ecommerce.payment.infrastructure.adapter.in.web.dto.PaymentResponse;
import com.ecommerce.payment.infrastructure.adapter.in.web.dto.RefundRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Gestion des paiements")
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {

    private final InitiatePaymentUseCase initiatePaymentUseCase;
    private final GetPaymentUseCase getPaymentUseCase;
    private final RefundPaymentUseCase refundPaymentUseCase;

    @PostMapping
    @Operation(summary = "Initier un paiement")
    public ResponseEntity<PaymentCreatedResponse> initiate(
            @Valid @RequestBody InitiatePaymentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        // Récupérer l'userId depuis le token JWT
        UUID userId = UUID.fromString(userDetails.getUsername());

        Payment payment = initiatePaymentUseCase.initiate(
                new InitiatePaymentUseCase.InitiatePaymentCommand(
                        request.orderId(),
                        userId,
                        request.amount(),
                        request.currency(),
                        request.paymentMethod()
                )
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(PaymentCreatedResponse.from(payment));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un paiement par ID")
    public ResponseEntity<PaymentResponse> getById(@PathVariable UUID id) {
        Payment payment = getPaymentUseCase.getById(id);
        return ResponseEntity.ok(PaymentResponse.from(payment));
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Récupérer le paiement d'une commande")
    public ResponseEntity<PaymentResponse> getByOrderId(@PathVariable UUID orderId) {
        Payment payment = getPaymentUseCase.getByOrderId(orderId);
        return ResponseEntity.ok(PaymentResponse.from(payment));
    }

    @PostMapping("/{id}/refund")
    @Operation(summary = "Rembourser un paiement")
    public ResponseEntity<PaymentResponse> refund(
            @PathVariable UUID id,
            @Valid @RequestBody RefundRequest request) {   // ← ajout du body
        Payment payment = refundPaymentUseCase.refund(id, request.reason());
        return ResponseEntity.ok(PaymentResponse.from(payment));
    }

    @PostMapping("/{id}/confirm")
    @Operation(summary = "Confirmer un paiement réussi")
    public ResponseEntity<PaymentResponse> confirmPayment(@PathVariable UUID id) {
        Payment payment = initiatePaymentUseCase.confirmPayment(id);
        return ResponseEntity.ok(PaymentResponse.from(payment));
    }
}