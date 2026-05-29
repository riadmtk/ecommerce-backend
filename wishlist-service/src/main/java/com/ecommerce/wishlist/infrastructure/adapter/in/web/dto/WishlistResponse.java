package com.ecommerce.wishlist.infrastructure.adapter.in.web.dto;

import com.ecommerce.wishlist.domain.model.Wishlist;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
public class WishlistResponse {
    private UUID id;
    private UUID userId;
    private Set<WishlistItemResponse> items;

    @Data
    @Builder
    public static class WishlistItemResponse {
        private UUID productId;
        private LocalDateTime addedAt;
        private boolean notifyOnRestock;
    }

    // A handy static factory method to map your Domain Model to this DTO
    public static WishlistResponse from(Wishlist wishlist) {
        return WishlistResponse.builder()
                .id(wishlist.getId())
                .userId(wishlist.getUserId())
                .items(wishlist.getItems().stream()
                        .map(item -> WishlistItemResponse.builder()
                                .productId(item.getProductId())
                                .addedAt(item.getAddedAt())
                                .notifyOnRestock(item.isNotifyOnRestock())
                                .build())
                        .collect(Collectors.toSet()))
                .build();
    }
}