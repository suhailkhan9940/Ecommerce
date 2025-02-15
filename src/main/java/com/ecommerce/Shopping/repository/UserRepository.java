package com.ecommerce.Shopping.repository;

import com.ecommerce.Shopping.model.UserDtls;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserDtls, Integer> {

    public UserDtls findByEmail(String email);
}
