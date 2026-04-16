package com.ecommerce.product.infrastructure.adapter.out.persistence;

import com.ecommerce.product.infrastructure.adapter.out.persistence.entity.ProductEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductJpaRepositoryTest {

    @Autowired
    private ProductJpaRepository productJpaRepository;

    @Test
    void shouldSaveAndFindProductEntity() {
        // --- ARRANGE ---
        ProductEntity entity = new ProductEntity();
        // ON SUPPRIME l'affectation manuelle de l'ID !
        entity.setName("Casque Bluetooth");
        entity.setDescription("Réduction de bruit active");
        entity.setPrice(new BigDecimal("299.99"));
        entity.setStockQuantity(15);
        entity.setActive(true);

        // --- ACT ---
        // On sauvegarde : Hibernate va générer l'UUID lui-même
        ProductEntity savedEntity = productJpaRepository.saveAndFlush(entity);

        // On cherche le produit en utilisant l'ID généré par Hibernate
        Optional<ProductEntity> foundEntity = productJpaRepository.findById(savedEntity.getId());

        // --- ASSERT ---
        assertTrue(foundEntity.isPresent());
        assertNotNull(foundEntity.get().getId()); // On vérifie que l'ID a bien été créé
        assertEquals("Casque Bluetooth", foundEntity.get().getName());
        assertEquals(new BigDecimal("299.99"), foundEntity.get().getPrice());
        assertEquals(15, foundEntity.get().getStockQuantity());
        assertTrue(foundEntity.get().isActive());

        assertNotNull(foundEntity.get().getCreatedAt());
        assertNotNull(foundEntity.get().getUpdatedAt());
    }

    @Test
    void shouldReturnEmptyWhenProductDoesNotExist() {
        // --- ACT ---
        Optional<ProductEntity> foundEntity = productJpaRepository.findById(UUID.randomUUID());

        // --- ASSERT ---
        assertTrue(foundEntity.isEmpty());
    }
}