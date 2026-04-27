package com.ecommerce.cart.domain.port.out;

import java.util.UUID;

public interface ProductClientPort {
    // Le domaine "Panier" n'a pas besoin de savoir à quoi ressemble un produit en entier.
    // Il a juste besoin d'une seule chose vitale : Le stock !
    int getAvailableStock(UUID productId);
}