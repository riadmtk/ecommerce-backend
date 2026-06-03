package com.ecommerce.product.infrastructure.adapter.out.persistence.repository;

import com.ecommerce.product.infrastructure.adapter.out.persistence.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SpringDataCategoryRepository extends JpaRepository<CategoryEntity, UUID> {
    boolean existsByName(String name);
}