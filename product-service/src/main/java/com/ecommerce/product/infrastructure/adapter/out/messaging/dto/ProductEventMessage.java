package com.ecommerce.product.infrastructure.adapter.out.messaging.dto;

import com.ecommerce.product.domain.model.Product;
import com.ecommerce.product.domain.model.ProductImage;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ProductEventMessage {
    private String eventType;
    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private int stockQuantity;
    private boolean active;

    // 🚀 NEW: Flat fields for the Category
    private UUID categoryId;
    private String categoryName;

    private List<String> imageUrls;

    public static ProductEventMessage fromProduct(String eventType, Product product) {
        List<String> extractedUrls = product.getImages() != null ?
                product.getImages().stream().map(ProductImage::getImageUrl).toList() : new ArrayList<>();

        // Safely extract category data
        UUID catId = product.getCategory() != null ? product.getCategory().getId() : null;
        String catName = product.getCategory() != null ? product.getCategory().getName() : null;

        return ProductEventMessage.builder()
                .eventType(eventType)
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .active(product.isActive())
                .categoryId(catId)       // ← Send ID
                .categoryName(catName)   // ← Send Name
                .imageUrls(extractedUrls)
                .build();
    }
}