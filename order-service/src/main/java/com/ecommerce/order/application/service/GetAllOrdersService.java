package com.ecommerce.order.application.service;
import com.ecommerce.order.domain.model.Order;
import com.ecommerce.order.domain.port.in.GetAllOrdersUseCase;
import com.ecommerce.order.domain.port.out.OrderRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAllOrdersService implements GetAllOrdersUseCase {
    private final OrderRepositoryPort orderRepository;
    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}