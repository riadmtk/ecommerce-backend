package com.ecommerce.order.infrastructure.adapter.out.client;

import com.ecommerce.order.domain.model.OrderItem;
import com.ecommerce.order.domain.port.out.CartServicePort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class CartServiceAdapter implements CartServicePort {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${cart.service.url}")
    private String cartServiceUrl;

    @Value("${product.service.url}")
    private String productServiceUrl;

    @Override
    public List<OrderItem> getCartItems(UUID userId, String token) {
        // 1️⃣ Appeler le cart-service
        String cartUrl = cartServiceUrl + "/api/v1/carts/my-cart";
        ResponseEntity<CartResponse> response;
        try {
            response = restTemplate.exchange(
                    cartUrl,
                    HttpMethod.GET,
                    new HttpEntity<>(createAuthHeaders(token)),
                    CartResponse.class
            );
        } catch (RestClientException e) {
            log.error("Erreur lors de l'appel au cart-service : {}", e.getMessage());
            throw new IllegalStateException("Impossible de récupérer le panier depuis le service panier", e);
        }

        if (response.getBody() == null || response.getBody().getItems() == null) {
            return new ArrayList<>();
        }

        // 2️⃣ Enrichir chaque article avec les détails du produit
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : response.getBody().getItems()) {
            ProductResponse product = getProductDetailsSafely(cartItem.getProductId(), token);
            BigDecimal unitPrice = product.getPrice() != null ? product.getPrice() : BigDecimal.ZERO;
            BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            String productName = product.getName() != null ? product.getName() : "Produit inconnu";

            orderItems.add(OrderItem.builder()
                    .productId(cartItem.getProductId())
                    .productName(productName)
                    .quantity(cartItem.getQuantity())
                    .unitPrice(unitPrice)
                    .totalPrice(totalPrice)
                    .build());
        }
        return orderItems;
    }

    private ProductResponse getProductDetailsSafely(UUID productId, String token) {
        String productUrl = productServiceUrl + "/api/v1/products/" + productId;
        try {
            ResponseEntity<ProductResponse> response = restTemplate.exchange(
                    productUrl,
                    HttpMethod.GET,
                    new HttpEntity<>(createAuthHeaders(token)),
                    ProductResponse.class
            );
            if (response.getBody() != null) {
                log.debug("Produit {} récupéré : name={}, price={}", productId, response.getBody().getName(), response.getBody().getPrice());
                return response.getBody();
            }
        } catch (RestClientException e) {
            log.warn("Erreur lors de la récupération du produit {} : {}", productId, e.getMessage());
        }
        // Fallback : produit par défaut
        log.warn("Aucune information produit pour {}, utilisation de valeurs par défaut", productId);
        ProductResponse fallback = new ProductResponse();
        fallback.setName("Produit " + productId.toString().substring(0, 8));
        fallback.setPrice(BigDecimal.ZERO);
        return fallback;
    }

    @Override
    public void clearCart(UUID userId, String token) {
        String url = cartServiceUrl + "/api/v1/carts/my-cart";
        restTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(createAuthHeaders(token)), Void.class);
    }

    private HttpHeaders createAuthHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
    }

    // --- DTOs internes ---
    @lombok.Data
    public static class CartResponse {
        private UUID id;
        private UUID userId;
        private List<CartItem> items;
    }

    @lombok.Data
    public static class CartItem {
        private UUID productId;
        private int quantity;
    }

    @lombok.Data
    public static class ProductResponse {
        private UUID id;
        private String name;
        private BigDecimal price;
    }
}