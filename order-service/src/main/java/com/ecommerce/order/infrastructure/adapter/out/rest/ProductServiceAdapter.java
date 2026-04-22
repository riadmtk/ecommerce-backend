package com.ecommerce.order.infrastructure.adapter.out.rest;

import com.ecommerce.order.domain.model.OrderItem;
import com.ecommerce.order.domain.port.out.ProductServiceClientPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@Component
public class ProductServiceAdapter implements ProductServiceClientPort {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${product.service.url}")
    private String productServiceUrl;

    @Override
    public void validateAndReserveStock(List<OrderItem> items) {
        String url = productServiceUrl + "/api/products/reserve";
        restTemplate.postForObject(url, items, Void.class);
    }

    @Override
    public void releaseStock(List<OrderItem> items) {
        String url = productServiceUrl + "/api/products/release";
        restTemplate.postForObject(url, items, Void.class);
    }
}