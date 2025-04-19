package com.agregator.backend.controller;

import com.agregator.backend.model.Tool;
import com.agregator.backend.service.ToolService;
import com.agregator.backend.repository.ToolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.PostMapping;
import com.agregator.backend.service.StorageService;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.agregator.backend.model.ToolUpdateRequest;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.agregator.backend.model.ToolCreateRequest;
import jakarta.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/tools")
@CrossOrigin(origins = "*")
public class ToolController {

    private final ToolService toolService;
    private final StorageService storageService;
    private final ToolRepository toolRepository;

    @Autowired
    public ToolController(ToolService toolService, StorageService storageService, ToolRepository toolRepository) {
        this.toolService = toolService;
        this.storageService = storageService;
        this.toolRepository = toolRepository;
    }

    @GetMapping
    public List<Tool> getTools(
        @RequestParam(value = "categoryName", required = false) List<String> categoryNames,
        @RequestParam(required = false, defaultValue = "newest") String sortBy,
        @RequestParam(required = false) String search,
        @RequestParam(required = false) List<String> pricing
    ) {
        return toolService.getTools(categoryNames, sortBy, search, pricing);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tool> getToolById(@PathVariable String id) {
        Optional<Tool> tool = toolService.getToolById(id);
        return tool.map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/image")
    public ResponseEntity<Tool> uploadToolImage(
        @PathVariable String id, 
        @RequestParam("file") MultipartFile file) {
            
        Tool tool = toolService.getToolById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tool not found with id: " + id));

        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot upload empty file.");
        }
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String uniqueFileName = "tool-images/" + id + "_" + UUID.randomUUID().toString() + fileExtension; 

        try {
            String publicUrl = storageService.uploadFile(file, uniqueFileName);
            
            tool.setImageUrl(publicUrl);
            
            Tool updatedTool = toolRepository.save(tool);

            return ResponseEntity.ok(updatedTool);
            
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload image.", e);
        }
    }

    // New endpoint to partially update a tool
    @PatchMapping("/{id}")
    public ResponseEntity<Tool> patchTool(
        @PathVariable String id,
        @RequestBody ToolUpdateRequest updateRequest) {
        
        Tool updatedTool = toolService.patchTool(id, updateRequest)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tool not found with id: " + id));
            
        return ResponseEntity.ok(updatedTool);
    }

    // New endpoint to create a tool
    @PostMapping
    public ResponseEntity<Tool> createTool(
        @Valid @RequestBody ToolCreateRequest createRequest) {
        
        Tool createdTool = toolService.createTool(createRequest);
        
        // Build the location URI for the newly created resource
        URI location = URI.create("/api/tools/" + createdTool.getId());
        
        // Return 201 Created status with Location header and response body
        return ResponseEntity.created(location).body(createdTool);
    }

    // Add more endpoints later (e.g., POST for adding tools, GET by ID, etc.)
} 