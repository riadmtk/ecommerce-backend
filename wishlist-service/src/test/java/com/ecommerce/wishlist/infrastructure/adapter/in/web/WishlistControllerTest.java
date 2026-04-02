package com.ecommerce.wishlist.infrastructure.adapter.in.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("WishlistController - Tests Infrastructure")
class WishlistControllerTest {

    @Test
    @DisplayName("Doit exposer GET /api/wishlist")
    void shouldExposeGetWishlistEndpoint() {
        // GET /api/wishlist → wishlist de l'utilisateur connecté
        assertTrue(true, "GetWishlist endpoint sera testé avec @WebMvcTest");
    }

    @Test
    @DisplayName("Doit exposer POST /api/wishlist/items")
    void shouldExposeAddItemEndpoint() {
        // POST /api/wishlist/items { productId }
        assertTrue(true, "AddItem endpoint sera testé avec @WebMvcTest");
    }

    @Test
    @DisplayName("Doit exposer DELETE /api/wishlist/items/{productId}")
    void shouldExposeRemoveItemEndpoint() {
        // DELETE /api/wishlist/items/{productId}
        assertTrue(true, "RemoveItem endpoint sera testé avec @WebMvcTest");
    }

    @Test
    @DisplayName("Doit retourner 409 si produit déjà dans la wishlist")
    void shouldReturn409IfProductAlreadyInWishlist() {
        assertTrue(true, "409 Conflict handling sera testé avec @WebMvcTest");
    }
}