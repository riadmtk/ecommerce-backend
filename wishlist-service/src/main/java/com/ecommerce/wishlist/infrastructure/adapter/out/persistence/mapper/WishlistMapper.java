package com.ecommerce.wishlist.infrastructure.adapter.out.persistence.mapper;

import com.ecommerce.wishlist.domain.model.Wishlist;
import com.ecommerce.wishlist.domain.model.WishlistItem;
import com.ecommerce.wishlist.infrastructure.adapter.out.persistence.entity.WishlistEntity;
import com.ecommerce.wishlist.infrastructure.adapter.out.persistence.entity.WishlistItemEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class WishlistMapper {

    public Wishlist toDomain(WishlistEntity entity) {
        return Wishlist.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .items(entity.getItems().stream()
                        .map(this::toDomainItem)
                        .collect(Collectors.toSet()))
                .build();
    }

    private WishlistItem toDomainItem(WishlistItemEntity entity) {
        return WishlistItem.builder()
                .productId(entity.getProductId())
                .addedAt(entity.getAddedAt())
                .notifyOnRestock(entity.isNotifyOnRestock())
                .build();
    }

    public WishlistEntity toEntity(Wishlist domain) {
        return WishlistEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .items(domain.getItems().stream()
                        .map(this::toEntityItem)
                        .collect(Collectors.toSet()))
                .build();
    }

    private WishlistItemEntity toEntityItem(WishlistItem domain) {
        return WishlistItemEntity.builder()
                .productId(domain.getProductId())
                .addedAt(domain.getAddedAt())
                .notifyOnRestock(domain.isNotifyOnRestock())
                .build();
    }
}