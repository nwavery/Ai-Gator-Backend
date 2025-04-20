package com.agregator.agregator_backend.services;

import com.agregator.backend.model.ToolApprovedEvent;
import com.agregator.backend.model.Category;
import com.agregator.backend.model.Tool;
import com.agregator.backend.repository.CategoryRepository;
import com.agregator.backend.repository.ToolRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class KafkaConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    private final ToolRepository toolRepository;
    private final CategoryRepository categoryRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public KafkaConsumerService(
            ToolRepository toolRepository,
            CategoryRepository categoryRepository,
            ObjectMapper objectMapper) {
        this.toolRepository = toolRepository;
        this.categoryRepository = categoryRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    @KafkaListener(topics = "${kafka.topic.tool.approved}", groupId = "${kafka.consumer.group-id}")
    public void consumeToolApprovedEvent(String message) {
        ToolApprovedEvent approvedEvent = null; // Declare here
        try {
            // Deserialize first
            approvedEvent = objectMapper.readValue(message, ToolApprovedEvent.class);
        } catch (Exception e) {
            // Handle potential deserialization error specifically
            logger.error("Failed to deserialize ToolApprovedEvent from message: {}", message, e);
            // Depending on error handling strategy, you might want to throw, send to DLQ, or just return
            throw new RuntimeException("Failed to deserialize Kafka message: " + e.getMessage(), e);
        }

        // Now proceed with processing, approvedEvent is effectively final for the lambda
        try {
            // Log the received event details
            logger.info("Received ToolApprovedEvent: {}", objectMapper.writeValueAsString(approvedEvent));

            // 1. Validate category exists
            logger.info("Looking up category with ID: {}", approvedEvent.getCategoryId());
            // Use a final variable inside the lambda if needed, or reference the effectively final approvedEvent
            final ToolApprovedEvent eventForLambda = approvedEvent; // Explicitly final copy for clarity
            Category category = categoryRepository.findById(approvedEvent.getCategoryId())
                    .orElseThrow(() -> {
                        String eventJson = "Error converting event to JSON";
                        try {
                            eventJson = objectMapper.writeValueAsString(eventForLambda); // Use final copy
                        } catch (Exception jsonEx) {
                            logger.error("Failed to serialize ToolApprovedEvent to JSON for error logging", jsonEx);
                        }
                        logger.error("Category not found for ID: {} in approved event: {}",
                                eventForLambda.getCategoryId(), // Use final copy
                                eventJson);
                        return new RuntimeException("Category not found for approved tool with category ID: " + eventForLambda.getCategoryId()); // Use final copy
                    });
            logger.info("Found category: {}", category.getName());

            // 2. Map event to Tool entity
            Tool tool = new Tool();
            tool.setName(approvedEvent.getName());
            tool.setDescription(approvedEvent.getDescription());
            tool.setImageUrl(approvedEvent.getImageUrl());
            tool.setWebsiteUrl(approvedEvent.getWebsiteUrl());
            tool.setCategory(category);

            // Handle potential other fields from event if needed:
            tool.setAffiliateLink(approvedEvent.getAffiliateLink());
            tool.setPricing(approvedEvent.getPricing());
            tool.setTagsFromList(approvedEvent.getTags());
            if (approvedEvent.getInitialUpvotes() != null) {
                tool.setUpvotes(approvedEvent.getInitialUpvotes());
            } else {
                tool.setUpvotes(0);
            }
            tool.setDateAdded(new java.util.Date());

            // 3. Save the new tool
            logger.info("Attempting to save tool: {}", approvedEvent.getName());
            Tool savedTool = toolRepository.save(tool);
            logger.info("Successfully saved tool with ID: {}", savedTool.getId());

        } catch (Exception e) {
            // Log the exception details including the event
            String eventDetails = "Failed to serialize event for logging";
            try {
                 eventDetails = objectMapper.writeValueAsString(approvedEvent);
            } catch (Exception logEx) {
                logger.error("Failed to serialize event to JSON during error logging", logEx);
            }
            logger.error("Error processing tool approved event: {}. Details: ", eventDetails, e);
            // Re-throw the exception so Spring Kafka's error handler can manage retries/DLQ
            throw new RuntimeException("Failed to process tool approved event: " + e.getMessage(), e);
        }
    }
} 