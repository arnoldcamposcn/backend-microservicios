package com.restaurant.order.infrastructure.adapter.out;

import com.restaurant.order.domain.model.Order;
import com.restaurant.order.domain.model.OrderItem;
import com.restaurant.order.domain.port.out.OrderRepositoryPort;
import com.restaurant.order.infrastructure.persistence.OrderEntity;
import com.restaurant.order.infrastructure.persistence.OrderItemEntity;
import com.restaurant.order.infrastructure.persistence.OrderItemR2dbcRepository;
import com.restaurant.order.infrastructure.persistence.OrderR2dbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepositoryPort {

    private final OrderR2dbcRepository orderRepository;
    private final OrderItemR2dbcRepository orderItemRepository;

    @Override
    public Mono<Order> save(Order order) {

        OrderEntity orderEntity = toOrderEntity(order);

        return orderRepository.save(orderEntity)
                .flatMap(savedOrder ->
                        saveItems(savedOrder.getId(), order.getItems())
                                .thenReturn(toOrderDomain(savedOrder, order.getItems()))
                );
    }

    @Override
    public Flux<Order> findAll() {

        return orderRepository.findAll()
                .flatMap(this::toDomainWithItems);
    }

    @Override
    public Flux<Order> findByStatus(String status) {

        return orderRepository.findByStatus(status)
                .flatMap(this::toDomainWithItems);
    }

    @Override
    public Mono<Order> findById(Long id) {

        return orderRepository.findById(id)
                .flatMap(this::toDomainWithItems);
    }

    @Override
    public Mono<Order> updateStatus(Long id, String status) {

        return orderRepository.findById(id)
                .flatMap(orderEntity -> {

                    orderEntity.setStatus(status);

                    return orderRepository.save(orderEntity);
                })
                .flatMap(this::toDomainWithItems);
    }

    private Mono<Void> saveItems(
            Long orderId,
            List<OrderItem> items) {

        return Flux.fromIterable(items)
                .map(item -> toItemEntity(orderId, item))
                .flatMap(orderItemRepository::save)
                .then();
    }

    private Mono<Order> toDomainWithItems(OrderEntity entity) {

        return orderItemRepository
                .findByOrderId(entity.getId())
                .map(this::toOrderItemDomain)
                .collectList()
                .map(items -> toOrderDomain(entity, items));
    }

    private Order toOrderDomain(
            OrderEntity entity,
            List<OrderItem> items) {

        Order order = new Order();

        order.setId(entity.getId());
        order.setCartId(entity.getCartId());
        order.setTotal(entity.getTotal());
        order.setStatus(entity.getStatus());
        order.setItems(items);

        return order;
    }

    private OrderItem toOrderItemDomain(
            OrderItemEntity entity) {

        OrderItem item = new OrderItem();

        item.setProductId(entity.getProductId());
        item.setProductName(entity.getProductName());
        item.setPrice(entity.getPrice());
        item.setQuantity(entity.getQuantity());
        item.setSubtotal(entity.getSubtotal());

        return item;
    }

    private OrderEntity toOrderEntity(Order order) {

        OrderEntity entity = new OrderEntity();

        entity.setId(order.getId());
        entity.setCartId(order.getCartId());
        entity.setTotal(order.getTotal());
        entity.setStatus(order.getStatus());

        return entity;
    }

    private OrderItemEntity toItemEntity(
            Long orderId,
            OrderItem item) {

        OrderItemEntity entity = new OrderItemEntity();

        entity.setOrderId(orderId);
        entity.setProductId(item.getProductId());
        entity.setProductName(item.getProductName());
        entity.setPrice(item.getPrice());
        entity.setQuantity(item.getQuantity());
        entity.setSubtotal(item.getSubtotal());

        return entity;
    }
}