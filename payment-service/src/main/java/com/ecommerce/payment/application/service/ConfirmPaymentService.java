package com.ecommerce.payment.application.service;

import com.ecommerce.payment.application.service.PaymentExternalEventPublisher.PaymentFailedEvent;
import com.ecommerce.payment.application.service.PaymentExternalEventPublisher.PaymentSuccessEvent;
import com.ecommerce.payment.domain.exception.PaymentAlreadyProcessedException;
import com.ecommerce.payment.domain.exception.PaymentNotFoundException;
import com.ecommerce.payment.domain.model.Payment;
import com.ecommerce.payment.domain.port.in.ConfirmPaymentUseCase;
import com.ecommerce.payment.domain.port.out.PaymentEventPublisherPort;
import com.ecommerce.payment.domain.port.out.PaymentRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfirmPaymentService implements ConfirmPaymentUseCase {

    private final PaymentRepositoryPort paymentRepository;
    private final PaymentEventPublisherPort eventPublisher;
    private final ApplicationEventPublisher applicationEventPublisher; // ← ajout

    @Override
    @Transactional  // ← IMPORTANT : nécessaire pour @TransactionalEventListener
    public Payment confirm(ConfirmPaymentCommand command) {
        log.info("Confirmation paiement transactionId={} success={}",
                command.transactionId(), command.success());

        Payment payment = paymentRepository.findByTransactionId(command.transactionId())
                .orElseThrow(() -> new PaymentNotFoundException(
                        "transactionId", command.transactionId()));

        if (!payment.isPending()) {
            throw new PaymentAlreadyProcessedException(command.transactionId());
        }

        if (command.success()) {
            Payment updated = payment.succeed(command.transactionId());
            Payment saved = paymentRepository.save(updated);
            eventPublisher.publishPaymentSucceeded(saved);

            // ← Publier l'événement Spring pour le Kafka externe
            applicationEventPublisher.publishEvent(new PaymentSuccessEvent(saved));
            return saved;
        } else {
            Payment updated = payment.fail(command.failureReason());
            Payment saved = paymentRepository.save(updated);
            eventPublisher.publishPaymentFailed(saved);

            // ← Publier l'événement Spring pour le Kafka externe
            applicationEventPublisher.publishEvent(new PaymentFailedEvent(saved));
            return saved;
        }
    }
}