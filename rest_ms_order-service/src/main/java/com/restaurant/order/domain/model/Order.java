package com.restaurant.order.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class Order {

    private Long id;
    private String cartId;
    private List<OrderItem> items;
    private BigDecimal total;
    private String status;
}