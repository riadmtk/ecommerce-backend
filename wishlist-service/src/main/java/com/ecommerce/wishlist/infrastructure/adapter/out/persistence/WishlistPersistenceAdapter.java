package com.ecommerce.wishlist.infrastructure.adapter.out.persistence;

import com.ecommerce.wishlist.domain.model.Wishlist;
import com.ecommerce.wishlist.domain.port.out.WishlistRepositoryPort;
import com.ecommerce.wishlist.infrastructure.adapter.out.persistence.entity.WishlistEntity;
import com.ecommerce.wishlist.infrastructure.adapter.out.persistence.mapper.WishlistMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WishlistPersistenceAdapter implements WishlistRepositoryPort {

    private final WishlistJpaRepository jpaRepository;
    private final WishlistMapper mapper;

    @Override
    public Optional<Wishlist> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId)
                .map(mapper::toDomain);
    }

    @Override
    public Wishlist save(Wishlist wishlist) {
        WishlistEntity entity = mapper.toEntity(wishlist);
        WishlistEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public List<Wishlist> findWishlistsNeedingRestockNotification(UUID productId) {
        return jpaRepository.findWishlistsNeedingRestockNotification(productId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}