package com.agregator.backend.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

// DTO for creating a new Tool
public class ToolCreateRequest {

    @NotEmpty(message = "Tool name cannot be empty")
    private String name;

    private String description; // Optional

    @NotEmpty(message = "Website URL cannot be empty")
    private String websiteUrl;

    private String affiliateLink; // Optional

    @NotNull(message = "Category ID cannot be null")
    private Long categoryId; // Required category ID

    private String pricing; // Optional
    private List<String> tags; // Optional
    private String imageUrl; // Optional, can be set later via upload
    // upvotes and dateAdded will be set by the server

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public String getAffiliateLink() {
        return affiliateLink;
    }

    public void setAffiliateLink(String affiliateLink) {
        this.affiliateLink = affiliateLink;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getPricing() {
        return pricing;
    }

    public void setPricing(String pricing) {
        this.pricing = pricing;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
} 