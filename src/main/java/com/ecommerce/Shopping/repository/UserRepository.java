package com.ecommerce.Shopping.repository;

import com.ecommerce.Shopping.model.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserDetails, Integer> {
}
