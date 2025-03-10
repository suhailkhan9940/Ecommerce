package com.ecommerce.Shopping.service;

import com.ecommerce.Shopping.model.UserDtls;

public interface UserService {
    public UserDtls saveUser(UserDtls user);

    public UserDtls getUserByEmail(String email);
}
