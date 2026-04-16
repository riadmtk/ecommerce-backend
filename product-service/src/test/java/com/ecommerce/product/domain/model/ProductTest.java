package com.ecommerce.product.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    void shouldCreateProductUsingBuilder() {
        // --- ARRANGE & ACT ---
        UUID expectedId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Product product = Product.builder()
                .id(expectedId)
                .name("Écran 4K")
                .description("Superbe écran")
                .price(new BigDecimal("399.99"))
                .stockQuantity(10) // On utilise bien un 'int'
                .active(true)      // On utilise bien un 'boolean'
                .createdAt(now)
                .updatedAt(now)
                .build();

        // --- ASSERT ---
        // On vérifie que Lombok a bien construit l'objet avec toutes les valeurs
        assertEquals(expectedId, product.getId());
        assertEquals("Écran 4K", product.getName());
        assertEquals("Superbe écran", product.getDescription());
        assertEquals(new BigDecimal("399.99"), product.getPrice());
        assertEquals(10, product.getStockQuantity());
        assertTrue(product.isActive());
        assertEquals(now, product.getCreatedAt());
        assertEquals(now, product.getUpdatedAt());
    }

    @Test
    void shouldUpdateProductStateWithSetters() {
        // --- ARRANGE ---
        Product product = Product.builder()
                .name("Clavier")
                .stockQuantity(5)
                .active(true)
                .build();

        // --- ACT ---
        // On simule ce que font tes UseCases (modification d'état)
        product.setStockQuantity(20);
        product.setActive(false);

        // --- ASSERT ---
        assertEquals(20, product.getStockQuantity());
        assertFalse(product.isActive());
    }
}