package com.ecommerce.product.application.service;

import com.ecommerce.product.domain.model.Product;
import com.ecommerce.product.domain.port.out.ProductRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteProductServiceTest {

    @Mock
    private ProductRepositoryPort productRepositoryPort;

    @InjectMocks
    private ProductService productService;

    @Test
    void shouldSoftDeleteProductSuccessfully() {
        // --- ARRANGE ---
        UUID productId = UUID.randomUUID();
        Product existingProduct = Product.builder()
                .id(productId)
                .name("Casque Audio")
                .active(true) // Le produit est initialement actif
                .build();

        // On simule que le produit est trouvé
        when(productRepositoryPort.findById(productId)).thenReturn(Optional.of(existingProduct));

        // --- ACT ---
        productService.deleteProduct(productId);

        // --- ASSERT ---
        // On vérifie que l'état de l'objet a bien été modifié
        assertFalse(existingProduct.isActive());

        // On vérifie que la sauvegarde a bien été déclenchée pour acter le Soft Delete
        verify(productRepositoryPort, times(1)).save(existingProduct);
    }

    @Test
    void shouldDoNothingWhenProductIsAlreadyInactive() {
        // --- ARRANGE ---
        UUID productId = UUID.randomUUID();
        Product alreadyInactiveProduct = Product.builder()
                .id(productId)
                .active(false) // Le produit est DEJA inactif
                .build();

        when(productRepositoryPort.findById(productId)).thenReturn(Optional.of(alreadyInactiveProduct));

        // --- ACT ---
        productService.deleteProduct(productId);

        // --- ASSERT ---
        // Vérifie qu'on a économisé une requête SQL : save() ne doit JAMAIS être appelé
        verify(productRepositoryPort, never()).save(any(Product.class));
    }
}