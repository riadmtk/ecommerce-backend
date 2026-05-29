package com.ecommerce.wishlist.infrastructure.adapter.out.client;

import com.ecommerce.wishlist.domain.exception.ProductNotFoundException;
import com.ecommerce.wishlist.domain.exception.ProductServiceUnavailableException;
import com.ecommerce.wishlist.domain.port.out.ProductServiceClientPort;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductServiceClientAdapter implements ProductServiceClientPort {

    private final ProductFeignClient feignClient;

    @Override
    public void validateProductExists(UUID productId) {
        try {
            log.debug("Vérification de l'existence du produit {} via Feign", productId);
            feignClient.checkProductExists(productId);

        } catch (FeignException.NotFound e) {
            // Si le product-service renvoie un 404, on lance notre exception métier
            log.warn("Le produit {} n'existe pas dans le catalogue.", productId);
            throw new ProductNotFoundException(productId);

        } catch (FeignException e) {
            // Pour toute autre erreur (500, connexion refusée, service down...)
            log.error("Le service produit est injoignable ou a renvoyé une erreur : {}", e.getMessage());
            throw new ProductServiceUnavailableException("Le service catalogue est temporairement indisponible.");
        }
    }
}