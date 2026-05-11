package com.ecommerce.search.infrastructure.adapter.out.elasticsearch.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Document(indexName = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDocument {

    @Id
    private UUID id;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String name;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String description;

    @Field(type = FieldType.Double)
    private BigDecimal price;

    @Field(type = FieldType.Integer)
    private int stockQuantity;

    @Field(type = FieldType.Boolean)
    private boolean active;

    // --- ADDED NEW FIELDS ---
    @Field(type = FieldType.Keyword)
    private String category;

    // index = false because we don't need to search BY the image URL text
    @Field(type = FieldType.Keyword, index = false)
    private List<String> imageUrls;
}