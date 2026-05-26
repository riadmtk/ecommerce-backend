package com.ecommerce.payment.application.service;

import com.ecommerce.payment.domain.exception.PaymentNotFoundException;
import com.ecommerce.payment.domain.model.Payment;
import com.ecommerce.payment.domain.model.PaymentStatus;
import com.ecommerce.payment.domain.port.in.InitiatePaymentUseCase;
import com.ecommerce.payment.domain.port.out.PaymentEventPublisherPort;
import com.ecommerce.payment.domain.port.out.PaymentProviderPort;
import com.ecommerce.payment.domain.port.out.PaymentRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class InitiatePaymentService implements InitiatePaymentUseCase {

    private final PaymentRepositoryPort paymentRepository;
    private final List<PaymentProviderPort> providers;
    private final PaymentEventPublisherPort eventPublisher;

    @Override
    @Transactional
    public Payment initiate(InitiatePaymentCommand command) {
        log.info("Initiation paiement orderId={} method={}",
                command.orderId(), command.paymentMethod());

        // 1. Créer l'objet Payment (statut PENDING, sans ID)
        Payment payment = Payment.create(
                command.orderId(),
                command.userId(),
                command.amount(),
                command.currency(),
                command.paymentMethod()
        );

        // 2. Trouver le provider adapté (Stripe, PayPal…)
        PaymentProviderPort provider = providers.stream()
                .filter(p -> p.supports(command.paymentMethod()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Provider non supporté : " + command.paymentMethod()));

        // 3. Appeler le provider pour créer le PaymentIntent (Stripe)
        PaymentProviderPort.PaymentIntentResult result =
                provider.createPaymentIntent(payment);

        // 4. Enrichir le paiement avec l'ID de transaction Stripe et le passer en SUCCESS
        payment = payment.withTransactionId(result.transactionId());
        payment = payment.succeed(result.transactionId());

        // 5. Sauvegarder en base
        Payment savedPayment = paymentRepository.save(payment);

        // 6. Publier l'événement PaymentCompleted (si nécessaire, mais la confirmation viendra plus tard)
        // eventPublisher.publishPaymentCompleted(savedPayment);

        // 7. Retourner avec le clientSecret (non stocké en BDD)
        return savedPayment.withClientSecret(result.clientSecret());
    }

    @Override
    @Transactional
    public Payment confirmPayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));

        payment = payment.withStatus(PaymentStatus.SUCCESS);
        Payment saved = paymentRepository.save(payment);

        // Publier événement Kafka
        eventPublisher.publishPaymentCompleted(saved);

        // OU appeler l'order-service directement (à choisir)
        // orderServiceClient.updateOrderStatus(payment.getOrderId(), "PAID");

        return saved;
    }


}