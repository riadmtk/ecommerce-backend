package com.ecommerce.payment.application.service;

import com.ecommerce.payment.domain.exception.PaymentNotFoundException;
import com.ecommerce.payment.domain.model.Payment;
import com.ecommerce.payment.domain.port.in.RefundPaymentUseCase;
import com.ecommerce.payment.domain.port.out.PaymentEventPublisherPort;
import com.ecommerce.payment.domain.port.out.PaymentProviderPort;
import com.ecommerce.payment.domain.port.out.PaymentRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefundPaymentService implements RefundPaymentUseCase {

    private final PaymentRepositoryPort paymentRepository;
    private final List<PaymentProviderPort> providers;
    private final PaymentEventPublisherPort eventPublisher;

    @Override
    public Payment refund(UUID paymentId) {
        log.info("Remboursement paymentId={}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));

        if (!payment.isSuccess()) {
            throw new IllegalStateException("Seul un paiement SUCCESS peut être remboursé");
        }

        PaymentProviderPort provider = providers.stream()
                .filter(p -> p.supports(payment.getPaymentMethod()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Provider non supporté"));

        provider.refund(payment);

        Payment refunded = payment.refund();
        Payment saved = paymentRepository.save(refunded);
        eventPublisher.publishPaymentRefunded(saved);
        return saved;
    }
}