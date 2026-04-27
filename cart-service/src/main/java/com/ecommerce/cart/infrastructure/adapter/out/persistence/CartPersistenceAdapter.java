package com.ecommerce.cart.infrastructure.adapter.out.persistence;

import com.ecommerce.cart.domain.model.Cart;
import com.ecommerce.cart.domain.port.out.CartRepositoryPort;
import com.ecommerce.cart.infrastructure.adapter.out.persistence.entity.CartEntity;
import com.ecommerce.cart.infrastructure.adapter.out.persistence.mapper.CartPersistenceMapper;
import com.ecommerce.cart.infrastructure.adapter.out.persistence.CartJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartPersistenceAdapter implements CartRepositoryPort {

    private final CartJpaRepository cartJpaRepository;
    private final CartPersistenceMapper mapper;

    @Override
    public Optional<Cart> findByUserId(UUID userId) {
        return cartJpaRepository.findByUserId(userId)
                .map(mapper::toDomain);
    }
    // Implement the new port method
    @Override
    public List<Cart> findCartsByProductId(UUID productId) {
        return cartJpaRepository.findCartsByProductId(productId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Cart save(Cart cart) {
        CartEntity entity = mapper.toEntity(cart);

        if (cart.getId() == null) {
            cartJpaRepository.findByUserId(cart.getUserId())
                    .ifPresent(existingEntity -> entity.setId(existingEntity.getId()));
        }

        CartEntity savedEntity = cartJpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }
}