package com.ecommerce.order.domain.port.out;

import com.ecommerce.order.domain.model.OrderItem;
import java.util.List;

public interface ProductServiceClientPort {
    void validateAndReserveStock(List<OrderItem> items);
    void releaseStock(List<OrderItem> items);
}