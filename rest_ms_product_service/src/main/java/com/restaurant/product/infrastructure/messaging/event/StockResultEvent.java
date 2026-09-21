package com.restaurant.product.infrastructure.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockResultEvent {

    private Long orderId;
    private String cartId;
    private String status;
    private String reason;
}
