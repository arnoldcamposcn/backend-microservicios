package com.restaurant.order.application.service;

import com.restaurant.order.domain.model.Order;
import com.restaurant.order.domain.model.OrderItem;
import com.restaurant.order.domain.port.in.OrderUseCase;
import com.restaurant.order.domain.port.out.OrderRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class OrderService implements OrderUseCase {

    private final OrderRepositoryPort orderRepositoryPort;

    @Override
    public Mono<Order> createOrder(Order order) {

        calculateSubtotals(order);
        calculateTotal(order);

        order.setStatus("RECEIVED");

        return orderRepositoryPort.save(order);
    }

    @Override
    public Flux<Order> getOrders() {
        return orderRepositoryPort.findAll();
    }

    @Override
    public Flux<Order> getOrdersByStatus(String status) {
        return orderRepositoryPort.findByStatus(status);
    }

    @Override
    public Mono<Order> getOrder(Long id) {
        return orderRepositoryPort.findById(id);
    }

    @Override
    public Mono<Order> updateStatus(Long id, String status) {
        return orderRepositoryPort.updateStatus(id, status);
    }

    private void calculateSubtotals(Order order) {

        order.getItems().forEach(item -> {

            BigDecimal subtotal = item.getPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));

            item.setSubtotal(subtotal);
        });
    }

    private void calculateTotal(Order order) {

        BigDecimal total = order.getItems()
                .stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotal(total);
    }
}