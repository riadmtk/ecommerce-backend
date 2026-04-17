package com.ecommerce.product.application.service;

import com.ecommerce.product.domain.model.Product;
import com.ecommerce.product.domain.port.in.UpdateProductCommand;
import com.ecommerce.product.domain.port.out.ProductRepositoryPort;
import com.ecommerce.product.domain.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateProductServiceTest {

    @Mock
    private ProductRepositoryPort productRepositoryPort;

    @InjectMocks
    private ProductService productService;

    @Test
    void shouldUpdateProductSuccessfully() {
        // --- ARRANGE ---
        UUID productId = UUID.randomUUID();

        // 1. La commande envoyée par l'utilisateur (les nouvelles données)
        UpdateProductCommand command = new UpdateProductCommand(
                productId,
                "MacBook Pro M3",
                "Laptop Apple mis à jour",
                new BigDecimal("2500.00"),
                15
        );

        // 2. Le produit existant en base de données (les anciennes données)
        Product existingProduct = Product.builder()
                .id(productId)
                .name("MacBook Pro M1") // Ancien nom
                .price(new BigDecimal("1200.00")) // Ancien prix
                .stockQuantity(5)
                .active(true)
                .build();

        // 3. On configure nos Mocks pour les DEUX appels à la base de données
        // D'abord, on simule que le produit est bien trouvé
        when(productRepositoryPort.findById(productId)).thenReturn(Optional.of(existingProduct));
        // Ensuite, on simule la sauvegarde
        when(productRepositoryPort.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        // --- ACT ---
        Product result = productService.execute(command);

        // --- ASSERT ---
        assertNotNull(result);
        assertEquals("MacBook Pro M3", result.getName()); // Vérifie que le nom a changé
        assertEquals(new BigDecimal("2500.00"), result.getPrice()); // Vérifie que le prix a changé
        assertEquals(15, result.getStockQuantity());

        // On vérifie que le port a bien été appelé pour lire PUIS pour écrire
        verify(productRepositoryPort, times(1)).findById(productId);
        verify(productRepositoryPort, times(1)).save(existingProduct);
    }

    @Test
    void shouldThrowExceptionWhenProductDoesNotExist() {
        // --- ARRANGE ---
        UUID productId = UUID.randomUUID();
        UpdateProductCommand command = new UpdateProductCommand(
                productId, "Test", "Test", new BigDecimal("100"), 10
        );

        // On simule une base de données vide (le produit n'existe pas)
        when(productRepositoryPort.findById(productId)).thenReturn(Optional.empty());

        // --- ACT & ASSERT ---
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            productService.execute(command);
        });

        assertTrue(exception.getMessage().contains("Product not found"));

        // Vérification cruciale : on s'assure qu'on n'a JAMAIS appelé save() sur un produit inexistant
        verify(productRepositoryPort, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenPriceIsNegative() {
        // --- ARRANGE ---
        UUID productId = UUID.randomUUID();
        // Prix invalide (-50)
        UpdateProductCommand command = new UpdateProductCommand(
                productId, "Test", "Test", new BigDecimal("-50.00"), 10
        );

        // --- ACT & ASSERT ---
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.execute(command);
        });

        assertEquals("Product price must be strictly positive.", exception.getMessage());

        // On vérifie que ça a planté AVANT même d'interroger la base de données !
        verify(productRepositoryPort, never()).findById(any());
        verify(productRepositoryPort, never()).save(any());
    }
}