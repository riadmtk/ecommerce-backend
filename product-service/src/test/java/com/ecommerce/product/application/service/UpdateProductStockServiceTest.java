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
class UpdateProductStockServiceTest {

    @Mock
    private ProductRepositoryPort productRepositoryPort;

    @InjectMocks
    private ProductService productService;

    // --- TESTS POUR LA DIMINUTION DE STOCK (DECREASE) ---

    @Test
    void shouldDecreaseStockSuccessfully() {
        // --- ARRANGE ---
        UUID productId = UUID.randomUUID();
        Product product = Product.builder()
                .id(productId)
                .name("Écran PC")
                .stockQuantity(50) // Stock initial : 50
                .active(true)
                .build();

        when(productRepositoryPort.findById(productId)).thenReturn(Optional.of(product));

        // --- ACT ---
        // On retire 10 articles
        productService.decreaseStock(productId, 10);

        // --- ASSERT ---
        // Le stock doit maintenant être de 40
        assertEquals(40, product.getStockQuantity());

        // On vérifie que le nouveau stock a bien été sauvegardé
        verify(productRepositoryPort, times(1)).save(product);
    }

    @Test
    void shouldThrowExceptionWhenInsufficientStock() {
        // --- ARRANGE ---
        UUID productId = UUID.randomUUID();
        Product product = Product.builder()
                .id(productId)
                .name("Écran PC")
                .stockQuantity(5) // Stock initial : 5
                .active(true)
                .build();

        when(productRepositoryPort.findById(productId)).thenReturn(Optional.of(product));

        // --- ACT & ASSERT ---
        // On essaie de retirer 10 articles (impossible car on n'en a que 5)
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            productService.decreaseStock(productId, 10);
        });

        assertEquals("Insufficient stock for product: Écran PC", exception.getMessage());

        // Règle d'or : en cas d'erreur métier, on s'assure qu'absolument RIEN n'a été sauvegardé en base
        verify(productRepositoryPort, never()).save(any(Product.class));
    }


    // --- TESTS POUR L'AUGMENTATION DE STOCK (INCREASE) ---

    @Test
    void shouldIncreaseStockSuccessfully() {
        // --- ARRANGE ---
        UUID productId = UUID.randomUUID();
        Product product = Product.builder()
                .id(productId)
                .name("Clavier")
                .stockQuantity(20) // Stock initial : 20
                .active(true)
                .build();

        when(productRepositoryPort.findById(productId)).thenReturn(Optional.of(product));

        // --- ACT ---
        // On reçoit une livraison de 15 articles
        productService.increaseStock(productId, 15);

        // --- ASSERT ---
        // Le stock doit maintenant être de 35
        assertEquals(35, product.getStockQuantity());

        verify(productRepositoryPort, times(1)).save(product);
    }

    // (Bonus implicite)
    // Pas besoin de tester l'exception "Product not found" car on l'a déjà
    // testée dans GetProductServiceTest et on sait que nos méthodes l'utilisent !
}