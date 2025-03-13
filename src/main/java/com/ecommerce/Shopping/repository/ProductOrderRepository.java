package com.ecommerce.Shopping.repository;

import com.ecommerce.Shopping.model.ProductOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductOrderRepository extends JpaRepository<ProductOrder, Integer> {

}
