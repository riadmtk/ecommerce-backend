package com.ecommerce.product.infrastructure.adapter.in.web;

import com.ecommerce.product.domain.model.Product;
import com.ecommerce.product.domain.port.in.*;
import com.ecommerce.product.infrastructure.adapter.in.web.dto.CreateProductRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @WebMvcTest charge UNIQUEMENT la couche Web (les Controllers), sans lancer la base de données !
@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc; // L'outil magique pour simuler des requêtes HTTP (notre faux Postman)

    @Autowired
    private ObjectMapper objectMapper; // Pour transformer nos objets Java en JSON

    @MockitoBean private CreateProductUseCase createProductUseCase;
    @MockitoBean private GetProductUseCase getProductUseCase;
    @MockitoBean private UpdateProductUseCase updateProductUseCase;
    @MockitoBean private DeleteProductUseCase deleteProductUseCase;
    @MockitoBean private HardDeleteProductUseCase hardDeleteProductUseCase;
    @MockitoBean private GetAdminProductUseCase getAdminProductUseCase;
    @MockitoBean private UpdateProductStockUseCase updateProductStockUseCase;

    @Test
    void shouldCreateProductAndReturn201Created() throws Exception {
        // --- ARRANGE ---
        CreateProductRequest request = new CreateProductRequest(
                "Clavier sans fil", "Super clavier", new BigDecimal("50.00"), 100
        );

        Product expectedProduct = Product.builder()
                .id(UUID.randomUUID())
                .name("Clavier sans fil")
                .price(new BigDecimal("50.00"))
                .stockQuantity(100)
                .active(true)
                .build();

        // On simule le succès du UseCase
        when(createProductUseCase.execute(any(CreateProductCommand.class))).thenReturn(expectedProduct);

        // --- ACT & ASSERT ---
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))) // Convertit le DTO en JSON
                .andExpect(status().isCreated()) // Vérifie le code HTTP 201
                .andExpect(jsonPath("$.name").value("Clavier sans fil")) // Vérifie le contenu du JSON renvoyé
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void shouldGetProductAndReturn200Ok() throws Exception {
        // --- ARRANGE ---
        UUID productId = UUID.randomUUID();
        Product product = Product.builder()
                .id(productId)
                .name("Écran")
                .price(new BigDecimal("200.00"))
                .active(true)
                .build();

        when(getProductUseCase.getProductById(productId)).thenReturn(product);

        // --- ACT & ASSERT ---
        mockMvc.perform(get("/api/v1/products/{id}", productId))
                .andExpect(status().isOk()) // Vérifie le code HTTP 200
                .andExpect(jsonPath("$.name").value("Écran"));
    }

    @Test
    void shouldDecreaseStockAndReturn204NoContent() throws Exception {
        // --- ARRANGE ---
        UUID productId = UUID.randomUUID();

        // --- ACT & ASSERT ---
        mockMvc.perform(patch("/api/v1/products/{id}/stock/decrease", productId)
                        .param("quantity", "5")) // Simule le paramètre d'URL (?quantity=5)
                .andExpect(status().isNoContent()); // Vérifie le code HTTP 204

        // On vérifie que le UseCase a bien été appelé avec les bons paramètres
        verify(updateProductStockUseCase).decreaseStock(eq(productId), eq(5));
    }

    @Test
    void shouldSoftDeleteProductAndReturn204NoContent() throws Exception {
        // --- ARRANGE ---
        UUID productId = UUID.randomUUID();

        // --- ACT & ASSERT ---
        mockMvc.perform(delete("/api/v1/products/{id}", productId))
                .andExpect(status().isNoContent()); // Vérifie le code HTTP 204

        verify(deleteProductUseCase).deleteProduct(productId);
    }
}