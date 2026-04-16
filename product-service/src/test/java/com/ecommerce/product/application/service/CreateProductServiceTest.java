package com.ecommerce.product.application.service;

import com.ecommerce.product.domain.model.Product;
import com.ecommerce.product.domain.port.in.CreateProductCommand;
import com.ecommerce.product.domain.port.out.ProductRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Active Mockito pour simuler la base de données
class CreateProductServiceTest {

    // 1. On "Mock" (simule) les dépendances sortantes
    @Mock
    private ProductRepositoryPort productRepositoryPort;

    // 2. On injecte les mocks dans le service qu'on veut réellement tester
    @InjectMocks
    private ProductService productService;

    @Test
    void shouldCreateProductSuccessfully() {
        // --- ARRANGE (Préparer) ---
        CreateProductCommand command = new CreateProductCommand(
                "iPhone 15",
                "Smartphone Apple",
                new BigDecimal("999.99"),
                50
        );

        // On dit au Mock : "Si on t'appelle avec n'importe quel Produit, retourne ce Produit"
        when(productRepositoryPort.save(any(Product.class))).thenAnswer(invocation -> {
            Product savedProduct = invocation.getArgument(0);
            return savedProduct; // Dans un vrai test avancé, on pourrait forcer un UUID ici
        });

        // --- ACT (Agir) ---
        Product result = productService.execute(command);

        // --- ASSERT (Vérifier) ---
        assertNotNull(result);
        assertEquals("iPhone 15", result.getName());
        assertEquals(new BigDecimal("999.99"), result.getPrice());
        assertEquals(50, result.getStockQuantity());
        assertTrue(result.isActive()); // On vérifie notre règle métier (actif par défaut)

        // On vérifie que la méthode save() a bien été appelée exactement 1 fois
        verify(productRepositoryPort, times(1)).save(any(Product.class));
    }

    @Test
    void shouldThrowExceptionWhenPriceIsNegativeOrZero() {
        // --- ARRANGE (Préparer) ---
        CreateProductCommand command = new CreateProductCommand(
                "Clavier",
                "Clavier mécanique",
                new BigDecimal("-10.00"), // Prix invalide !
                10
        );

        // --- ACT & ASSERT (Agir et Vérifier) ---
        // On vérifie que l'appel lève bien une IllegalArgumentException
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.execute(command);
        });

        assertEquals("Product price must be strictly positive.", exception.getMessage());

        // Très important : On vérifie que le système n'a JAMAIS essayé de sauvegarder en base
        verify(productRepositoryPort, never()).save(any(Product.class));
    }
}