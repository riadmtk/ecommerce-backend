package com.ecommerce.order.infrastructure.adapter.out.rest;

import com.ecommerce.order.domain.port.out.PaymentServicePort;
import com.ecommerce.order.infrastructure.adapter.out.rest.dto.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentServiceAdapter implements PaymentServicePort {
    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${payment.service.url}")
    private String paymentServiceUrl;

    @Override
    public PaymentResponse getPaymentByOrderId(UUID orderId) {
        Map<String, Object> map = restTemplate.getForObject(
                paymentServiceUrl + "/order/" + orderId,
                Map.class
        );
        return new PaymentResponse(
                UUID.fromString((String) map.get("id")),
                UUID.fromString((String) map.get("orderId")),
                (String) map.get("status")   // ← ajout du statut
        );
    }

    @Override
    public void refundPayment(UUID paymentId) {
        restTemplate.postForObject(paymentServiceUrl + "/" + paymentId + "/refund", null, Void.class);
    }
}
