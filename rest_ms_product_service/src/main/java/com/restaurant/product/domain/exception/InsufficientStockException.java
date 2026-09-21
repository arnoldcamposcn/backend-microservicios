package com.restaurant.product.domain.exception;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(Long productId) {
        super(
                "Product does not exist or has insufficient stock: "
                        + productId
        );
    }
}
