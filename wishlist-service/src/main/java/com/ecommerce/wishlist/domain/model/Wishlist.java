package com.ecommerce.wishlist.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class Wishlist {
    private UUID id;
    private UUID userId;
    @Builder.Default
    private Set<WishlistItem> items = new HashSet<>();

    public static Wishlist create(UUID userId) {
        return Wishlist.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .build();
    }

    public void addProduct(WishlistItem item) {
        // Sets automatically prevent duplicates because of our equals/hashCode!
        // But we can manually check if we want to throw a specific domain exception.
        this.items.removeIf(existing -> existing.getProductId().equals(item.getProductId()));
        this.items.add(item);
    }

    public void removeProduct(UUID productId) {
        this.items.removeIf(item -> item.getProductId().equals(productId));
    }
}