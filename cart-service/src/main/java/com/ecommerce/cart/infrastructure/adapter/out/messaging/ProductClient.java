package com.ecommerce.cart.infrastructure.adapter.out.messaging;

import com.ecommerce.cart.infrastructure.adapter.in.web.dto.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "product-service", url = "http://localhost:8082/api/v1/products")
public interface ProductClient {

    @GetMapping("/{id}")
    ProductResponse getProductById(@PathVariable("id") UUID id);
}