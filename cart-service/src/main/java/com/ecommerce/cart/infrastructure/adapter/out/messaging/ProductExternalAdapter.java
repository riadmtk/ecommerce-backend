package com.ecommerce.cart.infrastructure.adapter.out.messaging;

import com.ecommerce.cart.domain.port.out.ProductClientPort;
import com.ecommerce.cart.infrastructure.adapter.in.web.dto.ProductResponse;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProductExternalAdapter implements ProductClientPort {

    private final ProductClient productClient;

    @Override
    public int getAvailableStock(UUID productId) {
        try {
            ProductResponse response = productClient.getProductById(productId);
            return response.stockQuantity();

        } catch (FeignException.NotFound e) {
            throw new IllegalArgumentException("Product not found with ID: " + productId);
        } catch (FeignException e) {
            throw new IllegalStateException("Product service is currently unavailable. Please try again later.");
        }
    }
}