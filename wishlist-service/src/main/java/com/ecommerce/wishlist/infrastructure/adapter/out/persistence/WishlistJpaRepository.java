package com.ecommerce.wishlist.infrastructure.adapter.out.persistence;

import com.ecommerce.wishlist.infrastructure.adapter.out.persistence.entity.WishlistEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WishlistJpaRepository extends JpaRepository<WishlistEntity, UUID> {

    Optional<WishlistEntity> findByUserId(UUID userId);

    // 🎯 Highly optimized query to find ONLY users who opted in for a specific product restock
    @Query("SELECT w FROM WishlistEntity w JOIN w.items i WHERE i.productId = :productId AND i.notifyOnRestock = true")
    List<WishlistEntity> findWishlistsNeedingRestockNotification(@Param("productId") UUID productId);
}