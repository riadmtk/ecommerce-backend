package com.ecommerce.product.infrastructure.adapter.out.persistence;

import com.ecommerce.product.domain.model.Product;
import com.ecommerce.product.infrastructure.adapter.out.persistence.entity.ProductEntity;
import com.ecommerce.product.infrastructure.adapter.out.persistence.mapper.ProductPersistenceMapper;
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
class ProductPersistenceAdapterTest {

    // On simule la base de données (déjà testée ailleurs)
    @Mock
    private ProductJpaRepository jpaRepository;

    // On simule le traducteur (Mapper)
    @Mock
    private ProductPersistenceMapper mapper;

    // L'adaptateur qu'on veut réellement tester
    @InjectMocks
    private ProductPersistenceAdapter adapter;

    @Test
    void shouldSaveProductAndMapItCorrectly() {
        // --- ARRANGE ---
        Product domainProduct = Product.builder().id(UUID.randomUUID()).name("Domaine Produit").build();
        ProductEntity entityToSave = new ProductEntity();
        ProductEntity savedEntity = new ProductEntity(); // Simule le retour d'Hibernate avec les dates
        Product mappedDomainProduct = Product.builder().id(domainProduct.getId()).name("Domaine Produit").build();

        // On dicte le scénario de traduction
        // 1. Le domaine est converti en entité
        when(mapper.toEntity(domainProduct)).thenReturn(entityToSave);
        // 2. L'entité est sauvegardée (rappel : on utilise saveAndFlush !)
        when(jpaRepository.saveAndFlush(entityToSave)).thenReturn(savedEntity);
        // 3. L'entité sauvegardée est reconvertie en domaine pour le retour
        when(mapper.toDomain(savedEntity)).thenReturn(mappedDomainProduct);

        // --- ACT ---
        Product result = adapter.save(domainProduct);

        // --- ASSERT ---
        assertNotNull(result);

        // On vérifie que la chorégraphie s'est déroulée exactement dans le bon ordre
        verify(mapper, times(1)).toEntity(domainProduct);
        verify(jpaRepository, times(1)).saveAndFlush(entityToSave);
        verify(mapper, times(1)).toDomain(savedEntity);
    }

    @Test
    void shouldFindProductByIdAndMapItToDomain() {
        // --- ARRANGE ---
        UUID productId = UUID.randomUUID();
        ProductEntity foundEntity = new ProductEntity();
        Product expectedDomainProduct = Product.builder().id(productId).build();

        when(jpaRepository.findById(productId)).thenReturn(Optional.of(foundEntity));
        when(mapper.toDomain(foundEntity)).thenReturn(expectedDomainProduct);

        // --- ACT ---
        Optional<Product> result = adapter.findById(productId);

        // --- ASSERT ---
        assertTrue(result.isPresent());
        assertEquals(productId, result.get().getId());

        verify(jpaRepository, times(1)).findById(productId);
        verify(mapper, times(1)).toDomain(foundEntity);
    }

    @Test
    void shouldReturnEmptyOptionalWhenProductNotFound() {
        // --- ARRANGE ---
        UUID productId = UUID.randomUUID();

        // On simule qu'Hibernate ne trouve rien
        when(jpaRepository.findById(productId)).thenReturn(Optional.empty());

        // --- ACT ---
        Optional<Product> result = adapter.findById(productId);

        // --- ASSERT ---
        assertTrue(result.isEmpty());

        // On vérifie qu'on n'a pas essayé de mapper du vide !
        verify(mapper, never()).toDomain(any());
    }
}