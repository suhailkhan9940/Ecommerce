package com.ecommerce.Shopping.config;

import com.ecommerce.Shopping.model.UserDtls;
import com.ecommerce.Shopping.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserDtls user = userRepository.findByEmail(username);
        if(user == null){
            throw new UsernameNotFoundException("User Not Found");
        }
        return new CustomUser(user);
    }
}
