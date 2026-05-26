package com.ecommerce.payment.infrastructure.adapter.out.provider;

import com.ecommerce.payment.domain.exception.PaymentProviderException;
import com.ecommerce.payment.domain.model.Payment;
import com.ecommerce.payment.domain.model.PaymentMethod;
import com.ecommerce.payment.domain.port.out.PaymentProviderPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class PayPalAdapter implements PaymentProviderPort {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String clientId;
    private final String clientSecret;

    public PayPalAdapter(
            @Qualifier("paypalRestTemplate") RestTemplate restTemplate,
            @Qualifier("paypalBaseUrl") String baseUrl,
            @Qualifier("paypalClientId") String clientId,
            @Qualifier("paypalClientSecret") String clientSecret) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    // ─── 1. Obtenir le token OAuth2 ──────────────────────────────────────────

    private String getAccessToken() {
        String credentials = Base64.getEncoder()
                .encodeToString((clientId + ":" + clientSecret).getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Basic " + credentials);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    baseUrl + "/v1/oauth2/token",
                    HttpMethod.POST,
                    request,
                    Map.class
            );
            return (String) response.getBody().get("access_token");
        } catch (Exception e) {
            throw new PaymentProviderException("Impossible d'obtenir le token PayPal : " + e.getMessage(), e);
        }
    }

    // ─── 2. Créer un ordre PayPal ─────────────────────────────────────────────

    @Override
    @SuppressWarnings("unchecked")
    public PaymentIntentResult createPaymentIntent(Payment payment) {
        String accessToken = getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        Map<String, Object> amountMap = Map.of(
                "currency_code", payment.getCurrency().toUpperCase(),
                "value", payment.getAmount().toPlainString()
        );

        Map<String, Object> purchaseUnit = Map.of(
                "amount", amountMap,
                "custom_id", payment.getOrderId().toString()
        );

        Map<String, Object> appContext = Map.of(
                "return_url", "http://localhost:4200/payment/paypal/success",
                "cancel_url", "http://localhost:4200/payment/paypal/cancel",
                "brand_name", "E-Commerce App",
                "user_action", "PAY_NOW"
        );

        Map<String, Object> orderRequest = Map.of(
                "intent", "CAPTURE",
                "purchase_units", List.of(purchaseUnit),
                "application_context", appContext
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(orderRequest, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    baseUrl + "/v2/checkout/orders",
                    HttpMethod.POST,
                    request,
                    Map.class
            );

            Map<String, Object> body = response.getBody();
            String paypalOrderId = (String) body.get("id");

            // Récupérer le lien d'approbation
            List<Map<String, String>> links = (List<Map<String, String>>) body.get("links");
            String approvalUrl = links.stream()
                    .filter(link -> "approve".equals(link.get("rel")))
                    .map(link -> link.get("href"))
                    .findFirst()
                    .orElseThrow(() -> new PaymentProviderException("Lien d'approbation PayPal introuvable"));

            log.info("PayPal Order créé : id={}", paypalOrderId);
            // transactionId = PayPal Order ID, clientSecret = URL d'approbation
            return new PaymentIntentResult(paypalOrderId, approvalUrl);

        } catch (PaymentProviderException e) {
            throw e;
        } catch (Exception e) {
            throw new PaymentProviderException("Erreur création ordre PayPal : " + e.getMessage(), e);
        }
    }

    // ─── 3. Capturer un ordre (après approbation utilisateur) ────────────────

    @SuppressWarnings("unchecked")
    public String captureOrder(String paypalOrderId) {
        String accessToken = getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(Map.of(), headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    baseUrl + "/v2/checkout/orders/" + paypalOrderId + "/capture",
                    HttpMethod.POST,
                    request,
                    Map.class
            );

            Map<String, Object> body = response.getBody();
            List<Map<String, Object>> purchaseUnits =
                    (List<Map<String, Object>>) body.get("purchase_units");
            Map<String, Object> payments =
                    (Map<String, Object>) purchaseUnits.get(0).get("payments");
            List<Map<String, Object>> captures =
                    (List<Map<String, Object>>) payments.get("captures");

            String captureId = (String) captures.get(0).get("id");
            log.info("PayPal Order capturé : orderId={}, captureId={}", paypalOrderId, captureId);
            return captureId;

        } catch (Exception e) {
            throw new PaymentProviderException("Erreur capture PayPal : " + e.getMessage(), e);
        }
    }

    // ─── 4. Rembourser ───────────────────────────────────────────────────────

    @Override
    public void refund(Payment payment) {
        String accessToken = getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        // payment.getTransactionId() doit contenir le captureId (stocké après capture)
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(Map.of(), headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    baseUrl + "/v2/payments/captures/" + payment.getTransactionId() + "/refund",
                    HttpMethod.POST,
                    request,
                    Map.class
            );
            log.info("Remboursement PayPal réussi : refundId={}", response.getBody().get("id"));
        } catch (Exception e) {
            throw new PaymentProviderException("Erreur remboursement PayPal : " + e.getMessage(), e);
        }
    }

    @Override
    public boolean supports(PaymentMethod method) {
        return method == PaymentMethod.PAYPAL;
    }
}