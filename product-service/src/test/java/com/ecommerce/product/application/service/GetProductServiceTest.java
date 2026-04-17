package com.ecommerce.product.application.service;

import com.ecommerce.product.domain.model.Product;
import com.ecommerce.product.domain.port.out.ProductRepositoryPort;
import com.ecommerce.product.domain.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetProductServiceTest {

    @Mock
    private ProductRepositoryPort productRepositoryPort;

    @InjectMocks
    private ProductService productService;

    @Test
    void shouldReturnProductWhenActive() {
        // --- ARRANGE ---
        UUID productId = UUID.randomUUID();
        Product activeProduct = Product.builder()
                .id(productId)
                .name("Souris Gamer")
                .active(true) // Produit actif
                .build();

        when(productRepositoryPort.findById(productId)).thenReturn(Optional.of(activeProduct));

        // --- ACT ---
        Product result = productService.getProductById(productId);

        // --- ASSERT ---
        assertNotNull(result);
        assertEquals("Souris Gamer", result.getName());
    }

    @Test
    void shouldThrowExceptionWhenProductIsInactive() {
        // --- ARRANGE ---
        UUID productId = UUID.randomUUID();
        Product inactiveProduct = Product.builder()
                .id(productId)
                .name("Ancien Modèle")
                .active(false) // Produit inactif (Soft Deleted)
                .build();

        when(productRepositoryPort.findById(productId)).thenReturn(Optional.of(inactiveProduct));

        // --- ACT & ASSERT ---
        // On vérifie que la méthode "Client" refuse de renvoyer le produit
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            productService.getProductById(productId);
        });

        assertTrue(exception.getMessage().contains("Product not found"));
    }

    @Test
    void shouldReturnProductForAdminEvenIfInactive() {
        // --- ARRANGE ---
        UUID productId = UUID.randomUUID();
        Product inactiveProduct = Product.builder()
                .id(productId)
                .active(false) // Produit inactif
                .build();

        when(productRepositoryPort.findById(productId)).thenReturn(Optional.of(inactiveProduct));

        // --- ACT ---
        // On utilise la méthode "Admin" cette fois-ci !
        Product result = productService.getAdminProductById(productId);

        // --- ASSERT ---
        // L'admin a bien le droit de le voir
        assertNotNull(result);
        assertFalse(result.isActive());
    }
}