package com.restaurant.order.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI orderOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Microservicio de Gestión de Órdenes")
                        .description("""
                                Registra las órdenes generadas a partir de los \
                                productos seleccionados en un carrito. Calcula \
                                subtotales y total, almacena la orden en \
                                PostgreSQL y publica su creación mediante \
                                Kafka. La orden comienza en estado PENDING_STOCK \
                                y posteriormente cambia a CONFIRMED o REJECTED \
                                según el resultado de la validación del \
                                inventario.
                                """)
                        .version("1.0.0"));
    }
}
