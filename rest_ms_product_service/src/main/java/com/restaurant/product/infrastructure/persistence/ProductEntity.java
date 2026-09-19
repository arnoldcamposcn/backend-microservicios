package com.restaurant.product.infrastructure.persistence;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Getter
@Setter
@Table("products")
public class ProductEntity {

    @Id
    private Long id;

    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private Integer stock;
    private Boolean available;

}