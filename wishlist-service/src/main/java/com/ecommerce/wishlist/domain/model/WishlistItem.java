package com.ecommerce.wishlist.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class WishlistItem {
    private final UUID productId;
    private final LocalDateTime addedAt;
    private boolean notifyOnRestock; // 👈 Your brilliant addition

    // Equality is based ONLY on productId so we can use Sets to prevent duplicates
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WishlistItem that = (WishlistItem) o;
        return productId.equals(that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }

    public void enableRestockNotification() {
        this.notifyOnRestock = true;
    }

    public void disableRestockNotification() {
        this.notifyOnRestock = false;
    }
}