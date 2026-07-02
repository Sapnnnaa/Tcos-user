package com.telco.telco_co_app.security;

import com.telco.telco_co_app.model.User;
import com.telco.telco_co_app.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class UserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
    	
        User user = (User) userRepository.findByUserId(userId);
        
        if(user==null)
        	throw new UsernameNotFoundException("User not found with ticket number: " + userId);

        UserBuilder builder = org.springframework.security.core.userdetails.User.withUsername(user.getUserId());
        builder.password(user.getPassword());
        builder.roles("USER");

        return builder.build();
    }
    
}