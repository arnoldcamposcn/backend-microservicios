package com.restaurant.cart.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class Cart {

    private String id;
    private List<CartItem> items;
    private BigDecimal total;
}