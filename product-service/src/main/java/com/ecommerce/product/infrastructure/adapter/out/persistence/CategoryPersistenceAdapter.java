package com.ecommerce.product.infrastructure.adapter.out.persistence;

import com.ecommerce.product.domain.model.Category;
import com.ecommerce.product.domain.port.out.CategoryRepositoryPort;
import com.ecommerce.product.infrastructure.adapter.out.persistence.entity.CategoryEntity;
import com.ecommerce.product.infrastructure.adapter.out.persistence.mapper.CategoryMapper;
import com.ecommerce.product.infrastructure.adapter.out.persistence.mapper.ProductPersistenceMapper;
import com.ecommerce.product.infrastructure.adapter.out.persistence.repository.SpringDataCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CategoryPersistenceAdapter implements CategoryRepositoryPort {

    private final SpringDataCategoryRepository repository;

    // We will build a quick CategoryMapper below, or you can add these methods to your existing ProductMapper
    private final CategoryMapper mapper;

    @Override
    public Category save(Category category) {
        CategoryEntity entity = mapper.toEntity(category);
        CategoryEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Category> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Category> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}