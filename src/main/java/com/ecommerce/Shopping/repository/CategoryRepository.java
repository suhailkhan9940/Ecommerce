package com.ecommerce.Shopping.repository;

import com.ecommerce.Shopping.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category,Integer> {
    Boolean existsByName(String name);

    List<Category> findByIsActiveTrue();
}
