package com.ecommerce.product.infrastructure.adapter.out.persistence;

import com.ecommerce.product.infrastructure.adapter.out.persistence.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ProductJpaRepository extends JpaRepository<ProductEntity, UUID> {
    // Tu pourras ajouter des méthodes personnalisées ici plus tard (ex: findByName)
}