package com.agregator.backend.repository;

import com.agregator.backend.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Spring Data JPA provides basic CRUD methods like findAll(), findById(), save(), deleteById()

    // Custom query method to find a category by name (case-insensitive)
    Optional<Category> findByNameIgnoreCase(String name);

    // Custom query method to find categories by a list of names (case-insensitive)
    List<Category> findByNameInIgnoreCase(List<String> names);
} 