package com.ecommerce.wishlist.infrastructure.adapter.out.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

// Le "name" est le nom du service (utile si tu utilises Eureka/Consul)
// Le "url" est la variable de secours si tu fais des appels directs
@FeignClient(name = "product-service", url = "${product.service.url:http://localhost:8082}")
public interface ProductFeignClient {

    // On s'en fiche de désérialiser tout le produit, on veut juste savoir s'il répond 200 OK
    @GetMapping("/api/v1/products/{id}")
    void checkProductExists(@PathVariable("id") UUID id);
}