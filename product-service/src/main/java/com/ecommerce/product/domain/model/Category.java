package com.ecommerce.product.domain.model;

import lombok.*;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
    private UUID id;
    private String name;
    private String description;

    // We store the parent ID rather than the whole object to avoid infinite recursion loops in the domain
    private UUID parentId;
}