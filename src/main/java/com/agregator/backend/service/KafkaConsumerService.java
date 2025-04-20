package com.agregator.backend.service;

import com.agregator.backend.model.Category;
import com.agregator.backend.model.Tool;
import com.agregator.backend.model.ToolApprovedEvent;
import com.agregator.backend.repository.CategoryRepository;
import com.agregator.backend.repository.ToolRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

@Service
public class KafkaConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);
    private static final String APPROVED_TOPIC = "tool-approved-topic";

    private final ToolRepository toolRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public KafkaConsumerService(ToolRepository toolRepository, CategoryRepository categoryRepository) {
        this.toolRepository = toolRepository;
        this.categoryRepository = categoryRepository;
    }

    // Listener for the approved tools topic
    @KafkaListener(topics = APPROVED_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    @Transactional // Process message and save to DB within a transaction
    public void consumeToolApprovedEvent(@Payload ToolApprovedEvent approvedEvent) {
        logger.info("Received approved tool event from Kafka topic {}: {}", APPROVED_TOPIC, approvedEvent.getName());

        try {
            // 1. Validate category exists
            // Note: Category might have been deleted between submission and approval.
            // Decide how to handle this - skip, log error, move to dead-letter queue?
            Category category = categoryRepository.findById(approvedEvent.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found for approved tool '".concat(approvedEvent.getName()).concat("' with ID: ").concat(String.valueOf(approvedEvent.getCategoryId()))));
            
            // 2. Create new Tool entity from the approved event data
            Tool newTool = new Tool();
            newTool.setId(UUID.randomUUID().toString()); // Generate new ID
            newTool.setName(approvedEvent.getName());
            newTool.setDescription(approvedEvent.getDescription());
            newTool.setWebsiteUrl(approvedEvent.getWebsiteUrl());
            newTool.setAffiliateLink(approvedEvent.getAffiliateLink());
            newTool.setCategory(category);
            newTool.setPricing(approvedEvent.getPricing());
            newTool.setTagsFromList(approvedEvent.getTags());
            newTool.setImageUrl(approvedEvent.getImageUrl());
            newTool.setUpvotes(approvedEvent.getInitialUpvotes() != null ? approvedEvent.getInitialUpvotes() : 0);
            newTool.setDateAdded(new Date());

            // 3. Save the new tool to the database
            Tool savedTool = toolRepository.save(newTool);
            logger.info("Successfully saved approved tool '{}' with ID {} to database.", savedTool.getName(), savedTool.getId());

        } catch (Exception e) {
            logger.error("Error processing approved tool event for '{}': {}", approvedEvent.getName(), e.getMessage(), e);
            // Implement error handling strategy:
            // - Log and ignore: message might be lost if processing fails consistently.
            // - Throw exception: Causes container to stop polling (if using default error handler) or retry (if configured).
            // - Send to Dead Letter Topic (DLT): Recommended for persistent issues.
            // For now, rethrowing to leverage default Spring Kafka error handling (often involves logging)
            throw new RuntimeException("Failed to process approved tool event for " + approvedEvent.getName(), e);
        }
    }
} 