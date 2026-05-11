package com.ecommerce.product.domain.model;

import lombok.*;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImage {
    private UUID id;
    private String imageUrl; // Remember, this will now just be the filename (e.g., "b8b3...d57.webp")
    private int displayOrder; // Useful for sorting images on the frontend
    private boolean isPrimary; // To easily grab the main thumbnail
}