package com.telerikacademy.com.springdemo.controllers;

import com.telerikacademy.com.springdemo.models.User;
import com.telerikacademy.com.springdemo.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class AuthenticationHelper {

    public static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    private final UserService userService;

    @Autowired
    public AuthenticationHelper(UserService userService) {
        this.userService = userService;
    }

    public User tryGetUser(HttpHeaders headers){
        if (!headers.containsKey(AUTHORIZATION_HEADER_NAME)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "The requested resource requires authentication");
        }

        try {
            String username = headers.getFirst(AUTHORIZATION_HEADER_NAME);
            return userService.getByUserName(username);
        } catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username");
        }
    }
}
