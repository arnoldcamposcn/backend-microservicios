package com.restaurant.order.infrastructure.persistence;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Getter
@Setter
@Table("orders")
public class OrderEntity {

    @Id
    private Long id;

    private String cartId;

    private BigDecimal total;

    private String status;
}