package com.ecommerce.cart.infrastructure.adapter.out.persistence.mapper;

import com.ecommerce.cart.domain.model.Cart;
import com.ecommerce.cart.domain.model.CartItem;
import com.ecommerce.cart.infrastructure.adapter.out.persistence.entity.CartEntity;
import com.ecommerce.cart.infrastructure.adapter.out.persistence.entity.CartItemEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CartPersistenceMapper {

    public Cart toDomain(CartEntity entity) {
        if (entity == null) return null;

        return Cart.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .items(entity.getItems() != null ? entity.getItems().stream()
                        .map(item -> CartItem.builder()
                                .productId(item.getProductId())
                                .quantity(item.getQuantity())
                                .build())
                        .collect(Collectors.toList()) : new ArrayList<>())
                .build();
    }

    public CartEntity toEntity(Cart domain) {
        if (domain == null) return null;

        CartEntity entity = CartEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();

        if (domain.getItems() != null) {
            List<CartItemEntity> itemEntities = domain.getItems().stream()
                    .map(item -> CartItemEntity.builder()
                            .productId(item.getProductId())
                            .quantity(item.getQuantity())
                            .cart(entity) // 🎯 CRUCIAL : On lie l'enfant au parent pour JPA
                            .build())
                    .collect(Collectors.toList());

            entity.setItems(itemEntities);
        } else {
            entity.setItems(new ArrayList<>());
        }

        return entity;
    }
}