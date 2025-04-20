package com.agregator.backend.service;

import com.agregator.backend.model.ToolCreateRequest; // Import DTO
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);
    private static final String PENDING_TOPIC = "tool-pending-topic"; // Updated topic name

    // Inject KafkaTemplate specialized for sending ToolCreateRequest
    private final KafkaTemplate<String, ToolCreateRequest> kafkaTemplate;

    @Autowired
    public KafkaProducerService(KafkaTemplate<String, ToolCreateRequest> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // Method to send the DTO
    public void sendToolSubmission(ToolCreateRequest submission) {
        logger.info("Sending Tool Submission: {} to Kafka topic: {}", submission.getName(), PENDING_TOPIC);
        try {
            // Send the full submission DTO. Key could be null or e.g., the tool name or a generated UUID.
            kafkaTemplate.send(PENDING_TOPIC, submission);
            logger.info("Successfully sent tool submission for '{}' to Kafka.", submission.getName());
        } catch (Exception e) {
            logger.error("Error sending tool submission for '{}' to Kafka topic {}: {}", 
                         submission.getName(), PENDING_TOPIC, e.getMessage(), e);
            // Rethrow or handle as needed. For now, rethrow to signal failure upstream.
            throw new RuntimeException("Failed to send tool submission to Kafka", e);
        }
    }
} 