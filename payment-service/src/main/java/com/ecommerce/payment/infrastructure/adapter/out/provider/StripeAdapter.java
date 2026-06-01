package com.ecommerce.payment.infrastructure.adapter.out.provider;

import com.ecommerce.payment.domain.exception.PaymentProviderException;
import com.ecommerce.payment.domain.model.Payment;
import com.ecommerce.payment.domain.model.PaymentMethod;
import com.ecommerce.payment.domain.port.out.PaymentProviderPort;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StripeAdapter implements PaymentProviderPort {

    @Value("${stripe.api-key}")
    private String apiKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = apiKey;
    }

    @Override
    public PaymentIntentResult createPaymentIntent(Payment payment) {
        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(payment.getAmount()
                            .multiply(java.math.BigDecimal.valueOf(100))
                            .longValue())
                    .setCurrency(payment.getCurrency().toLowerCase())
                    .putMetadata("orderId", payment.getOrderId().toString())
                    .putMetadata("userId", payment.getUserId().toString())
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .setAllowRedirects(
                                            PaymentIntentCreateParams
                                                    .AutomaticPaymentMethods
                                                    .AllowRedirects.NEVER)
                                    .build()
                    )
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);
            log.info("Stripe PaymentIntent créé : {}", intent.getId());
            return new PaymentIntentResult(intent.getId(), intent.getClientSecret());

        } catch (StripeException e) {
            throw new PaymentProviderException("Erreur Stripe : " + e.getMessage(), e);
        }
    }

    @Override
    public void refund(Payment payment) {
        try {
            log.info("Tentative de remboursement Stripe pour PaymentIntent = {}", payment.getTransactionId());
            RefundCreateParams params = RefundCreateParams.builder()
                    .setPaymentIntent(payment.getTransactionId())
                    .build();
            Refund refund = Refund.create(params);
            log.info("Remboursement Stripe réussi : {}", refund.getId());
        } catch (StripeException e) {
            log.error("Erreur remboursement Stripe", e);
            throw new PaymentProviderException("Erreur remboursement Stripe : " + e.getMessage(), e);
        }
    }

    @Override
    public boolean supports(PaymentMethod method) {
        return method == PaymentMethod.STRIPE;
    }
}