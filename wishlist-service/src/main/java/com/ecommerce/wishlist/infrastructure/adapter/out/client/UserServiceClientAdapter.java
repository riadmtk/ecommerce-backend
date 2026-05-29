package com.ecommerce.wishlist.infrastructure.adapter.out.client;

import com.ecommerce.wishlist.domain.port.out.UserServiceClientPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserServiceClientAdapter implements UserServiceClientPort {

    private final UserFeignClient userFeignClient;

    @Override
    public String getUserEmail(UUID userId) {
        try {
            return userFeignClient.getUserById(userId).email();
        } catch (Exception e) {
            log.error("❌ Impossible de récupérer l'email pour l'utilisateur {}: {}", userId, e.getMessage());
            // Si le user-service plante, on renvoie une adresse de secours pour ne pas crasher Kafka
            return "support@ton-ecommerce.com";
        }
    }
}