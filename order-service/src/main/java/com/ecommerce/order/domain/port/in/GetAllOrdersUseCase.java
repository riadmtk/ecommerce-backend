package com.ecommerce.order.domain.port.in;
import com.ecommerce.order.domain.model.Order;
import java.util.List;

public interface GetAllOrdersUseCase {
    List<Order> getAllOrders();
}