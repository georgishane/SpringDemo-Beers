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
        String username = headers.getFirst(AUTHORIZATION_HEADER_NAME);
        if (username == null || username.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing or empty Authorization header");
        }

        try {
            User user = userService.getByUserName(username);
            if (!user.hasRole("ADMIN")) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin privileges required");
            }
            return user;
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username");
        }
    }
}
