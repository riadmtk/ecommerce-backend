package com.ecommerce.order.infrastructure.adapter.out.persistence;

import com.ecommerce.order.infrastructure.adapter.out.persistence.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID> {
    List<OrderEntity> findByUserId(UUID userId);
}