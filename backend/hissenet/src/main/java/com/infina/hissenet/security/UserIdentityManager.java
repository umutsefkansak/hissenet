package com.infina.hissenet.security;

import com.infina.hissenet.entity.Employee;
import com.infina.hissenet.exception.employee.UserNotFoundException;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
public class UserIdentityManager {
    public Employee getUser(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal instanceof Employee user){
            return user;
        }else {
            throw new UserNotFoundException("User: ");
        }
    }
}
