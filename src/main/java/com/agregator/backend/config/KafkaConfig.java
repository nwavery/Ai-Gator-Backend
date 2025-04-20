package com.agregator.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.converter.JsonMessageConverter;
import org.springframework.kafka.support.converter.RecordMessageConverter;

@Configuration
public class KafkaConfig {

    // This bean provides a message converter that uses Jackson for JSON serialization/
    // deserialization. It's used by KafkaTemplate and @KafkaListener.
    // It helps handle type information using headers, making deserialization more robust.
    @Bean
    public RecordMessageConverter converter() {
        return new JsonMessageConverter();
    }

    // You can add other Kafka related beans here if needed, 
    // e.g., custom KafkaListenerContainerFactory for error handling.
} 