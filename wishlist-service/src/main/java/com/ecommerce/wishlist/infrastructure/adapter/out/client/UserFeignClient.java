package com.ecommerce.wishlist.infrastructure.adapter.out.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "user-service", url = "${user.service.url:http://localhost:8081}")
public interface UserFeignClient {

    // 🎯 On utilise la vraie route, vers le nouvel endpoint interne !
    @GetMapping("/api/users/internal/{id}/email")
    UserResponse getUserById(@PathVariable("id") UUID id);

    record UserResponse(UUID id, String email) {}
}