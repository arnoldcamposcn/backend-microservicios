package com.restaurant.product.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI productOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Microservicio de Inventario")
                        .description("""
                                Administra el catálogo de productos disponibles \
                                en el menú del restaurante, incluyendo nombre, \
                                descripción, categoría, precio, disponibilidad \
                                y stock. Cuando se crea una orden, valida y \
                                descuenta el inventario de forma transaccional \
                                e idempotente. Posteriormente publica en Kafka \
                                si el stock fue confirmado o rechazado.
                                """)
                        .version("1.0.0"));
    }
}