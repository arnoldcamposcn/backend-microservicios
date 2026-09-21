package com.restaurant.product.domain.model;

public record StockAdjustment(
        Long productId,
        Integer quantity
) {
}
