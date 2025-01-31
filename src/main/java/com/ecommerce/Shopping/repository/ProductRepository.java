package com.ecommerce.Shopping.repository;

import com.ecommerce.Shopping.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product,Integer> {
}
