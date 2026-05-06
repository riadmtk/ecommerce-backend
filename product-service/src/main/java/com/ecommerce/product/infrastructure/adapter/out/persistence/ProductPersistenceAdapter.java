package com.ecommerce.product.infrastructure.adapter.out.persistence;

import com.ecommerce.product.domain.model.Product;
import com.ecommerce.product.domain.port.out.ProductRepositoryPort;
import com.ecommerce.product.infrastructure.adapter.out.persistence.mapper.ProductPersistenceMapper;
import com.ecommerce.product.infrastructure.adapter.out.persistence.ProductJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductPersistenceAdapter implements ProductRepositoryPort {

    private final ProductJpaRepository jpaRepository;
    private final ProductPersistenceMapper mapper;

    @Override
    public Product save(Product product) {
        return mapper.toDomain(jpaRepository.saveAndFlush(mapper.toEntity(product)));
    }

    @Override
    public List<Product> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Product> findAllSortedByDate() {
        return jpaRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}