package com.telerikacademy.com.springdemo.controllers.debug;

import com.telerikacademy.com.springdemo.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/debug")
public class DebugController {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Autowired
    public DebugController(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @GetMapping("/token-test")
    public String getShortLivedToken() {
        var userDetails = userDetailsService.loadUserByUsername("jack");
        return jwtUtil.generateToken(userDetails, 5000); // Token valid for 5 seconds
    }
}
