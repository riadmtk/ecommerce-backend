package com.ecommerce.cart.infrastructure.adapter.out.persistence;

import com.ecommerce.cart.infrastructure.adapter.out.persistence.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartJpaRepository extends JpaRepository<CartEntity, UUID> {
    Optional<CartEntity> findByUserId(UUID userId);

    @Query("SELECT DISTINCT c FROM CartEntity c JOIN c.items i WHERE i.productId = :productId")
    List<CartEntity> findCartsByProductId(@Param("productId") UUID productId);
}