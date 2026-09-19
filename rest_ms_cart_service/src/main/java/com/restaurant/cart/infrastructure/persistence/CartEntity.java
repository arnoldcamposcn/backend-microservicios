package com.restaurant.cart.infrastructure.persistence;

import com.restaurant.cart.domain.model.CartItem;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CartEntity {

    private String id;
    private List<CartItem> items;
    private BigDecimal total;
}