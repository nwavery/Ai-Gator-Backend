package com.agregator.backend.repository;

import com.agregator.backend.model.Tool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ToolRepository extends JpaRepository<Tool, String>, JpaSpecificationExecutor<Tool> {
    // JpaRepository provides basic CRUD (save, findById, findAll, deleteById, etc.)
    // JpaSpecificationExecutor provides find* methods accepting Specification 
    // (e.g., findAll(Specification<T> spec, Sort sort))
} 