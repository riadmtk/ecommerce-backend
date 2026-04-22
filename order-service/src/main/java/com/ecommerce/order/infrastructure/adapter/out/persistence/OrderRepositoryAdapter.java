package com.ecommerce.order.infrastructure.adapter.out.persistence;

import com.ecommerce.order.domain.model.Order;
import com.ecommerce.order.domain.port.out.OrderRepositoryPort;
import com.ecommerce.order.infrastructure.adapter.out.persistence.entity.OrderEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepositoryPort {
    private final OrderJpaRepository jpaRepository;
    private final OrderMapper mapper;

    @Override
    public Order save(Order order) {
        OrderEntity entity = mapper.toEntity(order);
        OrderEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Order> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}