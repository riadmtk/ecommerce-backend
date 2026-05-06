package com.ecommerce.order.infrastructure.adapter.out.rest;

import com.ecommerce.order.domain.model.OrderItem;
import com.ecommerce.order.domain.port.out.ProductServiceClientPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ProductServiceAdapter implements ProductServiceClientPort {

    @Override
    public void validateAndReserveStock(List<OrderItem> items) {
        // ⚠️ Validation de stock désactivée pendant les tests
        log.warn("🔧 Validation du stock ignorée pour {} articles", items.size());
    }

    @Override
    public void releaseStock(List<OrderItem> items) {
        log.warn("🔧 Libération du stock ignorée pour {} articles", items.size());
    }
}