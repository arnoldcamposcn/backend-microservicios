package com.restaurant.product.infrastructure.messaging.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class StockKafkaConfig {

    public static final String STOCK_CONFIRMED_TOPIC =
            "restaurant.stock.confirmed";
    public static final String STOCK_REJECTED_TOPIC =
            "restaurant.stock.rejected";

    @Bean
    public NewTopic stockConfirmedTopic() {
        return topic(STOCK_CONFIRMED_TOPIC);
    }

    @Bean
    public NewTopic stockRejectedTopic() {
        return topic(STOCK_REJECTED_TOPIC);
    }

    private NewTopic topic(String name) {
        return TopicBuilder
                .name(name)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
