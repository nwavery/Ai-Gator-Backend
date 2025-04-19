package com.agregator.backend.service;

import com.agregator.backend.model.Tool;
import com.agregator.backend.model.ToolUpdateRequest;
import com.agregator.backend.model.ToolCreateRequest;

import java.util.List;
import java.util.Optional;

public interface ToolService {
    List<Tool> getTools(List<String> categoryNames, String sortBy, String search, List<String> pricing);
    Optional<Tool> getToolById(String id);
    
    // Method to partially update a tool
    Optional<Tool> patchTool(String id, ToolUpdateRequest updateRequest);
    
    // Method to create a new tool
    Tool createTool(ToolCreateRequest createRequest);
} 