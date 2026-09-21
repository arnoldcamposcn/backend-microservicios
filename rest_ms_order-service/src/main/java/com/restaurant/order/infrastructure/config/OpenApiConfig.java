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
                        .title("Microservicio de Órdenes")
                        .description("""
                                API reactiva responsable del ciclo de vida de \
                                las órdenes del restaurante. Permite crear y \
                                consultar órdenes, filtrarlas por estado y \
                                actualizar su estado. Al crear una orden la \
                                persiste en PostgreSQL y publica un evento en \
                                Kafka; posteriormente actualiza la orden como \
                                confirmada o rechazada según el resultado del \
                                procesamiento de stock.
                                """)
                        .version("1.0.0"));
    }
}
