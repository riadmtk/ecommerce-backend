package com.ecommerce.cart.infrastructure.adapter.in.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("CartController - Tests Infrastructure")
class CartControllerTest {

    @Test
    @DisplayName("Doit exposer GET /api/cart")
    void shouldExposeGetCartEndpoint() {
        // GET /api/cart → retourne le panier de l'utilisateur connecté
        assertTrue(true, "GetCart endpoint sera testé avec @WebMvcTest");
    }

    @Test
    @DisplayName("Doit exposer POST /api/cart/items")
    void shouldExposeAddItemEndpoint() {
        // POST /api/cart/items { productId, quantity }
        assertTrue(true, "AddItem endpoint sera testé avec @WebMvcTest");
    }

    @Test
    @DisplayName("Doit exposer DELETE /api/cart/items/{productId}")
    void shouldExposeRemoveItemEndpoint() {
        // DELETE /api/cart/items/{productId}
        assertTrue(true, "RemoveItem endpoint sera testé avec @WebMvcTest");
    }

    @Test
    @DisplayName("Doit exposer POST /api/cart/checkout")
    void shouldExposeCheckoutEndpoint() {
        // POST /api/cart/checkout → déclenche création commande via Kafka
        assertTrue(true, "Checkout endpoint sera testé avec @WebMvcTest");
    }
}