package com.ecommerce.Shopping.service.impl;

import com.ecommerce.Shopping.model.UserDetails;
import com.ecommerce.Shopping.repository.UserRepository;
import com.ecommerce.Shopping.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails saveUser(UserDetails user) {
        UserDetails saveUser = userRepository.save(user);
        return saveUser;
    }
}
