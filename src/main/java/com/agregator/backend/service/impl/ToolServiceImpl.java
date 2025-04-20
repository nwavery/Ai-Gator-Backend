package com.agregator.backend.service.impl;

import com.agregator.backend.model.Category;
import com.agregator.backend.model.Tool;
import com.agregator.backend.model.ToolUpdateRequest;
import com.agregator.backend.model.ToolCreateRequest;
import com.agregator.backend.repository.ToolRepository;
import com.agregator.backend.service.ToolService;
import com.agregator.backend.service.KafkaProducerService;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.agregator.backend.repository.CategoryRepository;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ToolServiceImpl implements ToolService {

    private final ToolRepository toolRepository;
    private final CategoryRepository categoryRepository;
    private final KafkaProducerService kafkaProducerService;

    @Autowired
    public ToolServiceImpl(ToolRepository toolRepository, CategoryRepository categoryRepository, KafkaProducerService kafkaProducerService) {
        this.toolRepository = toolRepository;
        this.categoryRepository = categoryRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    @Override
    public List<Tool> getTools(List<String> categoryNames, String sortBy, String search, List<String> pricing) {
        Sort sort = createSort(sortBy);
        
        Specification<Tool> spec = createSpecification(categoryNames, search, pricing);

        return toolRepository.findAll(spec, sort);
    }

    private Specification<Tool> createSpecification(List<String> categoryNames, String search, List<String> pricing) {
        return (Root<Tool> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (categoryNames != null && !categoryNames.isEmpty()) {
                Join<Tool, Category> categoryJoin = root.join("category");
                List<String> lowerCaseCategoryNames = categoryNames.stream().map(String::toLowerCase).toList();
                predicates.add(cb.lower(categoryJoin.get("name")).in(lowerCaseCategoryNames));
            }

            if (StringUtils.hasText(search)) {
                String likePattern = "%" + search.toLowerCase() + "%";
                Predicate namePredicate = cb.like(cb.lower(root.get("name")), likePattern);
                Predicate descriptionPredicate = cb.like(cb.lower(root.get("description")), likePattern);
                predicates.add(cb.or(namePredicate, descriptionPredicate));
            }
            
            if (pricing != null && !pricing.isEmpty()) {
                List<String> lowerCasePricing = pricing.stream().map(String::toLowerCase).toList();
                predicates.add(cb.lower(root.get("pricing")).in(lowerCasePricing));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Override
    public Optional<Tool> getToolById(String id) {
        return toolRepository.findById(id);
    }

    @Override
    public Optional<Tool> patchTool(String id, ToolUpdateRequest updateRequest) {
        Optional<Tool> existingToolOptional = toolRepository.findById(id);
        
        if (existingToolOptional.isPresent()) {
            Tool existingTool = existingToolOptional.get();
            
            if (StringUtils.hasText(updateRequest.getName())) {
                existingTool.setName(updateRequest.getName());
            }
            if (updateRequest.getDescription() != null) {
                existingTool.setDescription(updateRequest.getDescription());
            }
            if (updateRequest.getWebsiteUrl() != null) {
                existingTool.setWebsiteUrl(updateRequest.getWebsiteUrl());
            }
            if (updateRequest.getAffiliateLink() != null) {
                existingTool.setAffiliateLink(updateRequest.getAffiliateLink());
            }
            if (updateRequest.getPricing() != null) {
                existingTool.setPricing(updateRequest.getPricing());
            }
            if (updateRequest.getImageUrl() != null) {
                existingTool.setImageUrl(updateRequest.getImageUrl());
            }
            
            if (updateRequest.getCategoryId() != null) {
                Category newCategory = categoryRepository.findById(updateRequest.getCategoryId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "Category not found with id: " + updateRequest.getCategoryId()));
                existingTool.setCategory(newCategory);
            }
            
            if (updateRequest.getTags() != null) {
                existingTool.setTagsFromList(updateRequest.getTags());
            }
            
            if (updateRequest.getUpvotes() != null) {
                existingTool.setUpvotes(updateRequest.getUpvotes());
            }

            return Optional.of(toolRepository.save(existingTool));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void submitToolForApproval(ToolCreateRequest createRequest) {
        categoryRepository.findById(createRequest.getCategoryId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Category not found with id: " + createRequest.getCategoryId()));

        kafkaProducerService.sendToolSubmission(createRequest);
    }

    private Sort createSort(String sortBy) {
        switch (sortBy.toLowerCase()) {
            case "upvoted":
                return Sort.by(Sort.Direction.DESC, "upvotes");
            case "name_asc":
                return Sort.by(Sort.Order.asc("name").ignoreCase());
            case "name_desc":
                return Sort.by(Sort.Order.desc("name").ignoreCase());
            case "oldest":
                return Sort.by(Sort.Direction.ASC, "dateAdded");
            case "newest":
            default:
                return Sort.by(Sort.Direction.DESC, "dateAdded");
        }
    }
} 