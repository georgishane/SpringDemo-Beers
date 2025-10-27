package com.telerikacademy.com.springdemo.controllers;

import com.telerikacademy.com.springdemo.models.*;
import com.telerikacademy.com.springdemo.repositories.RoleRepository;
import com.telerikacademy.com.springdemo.security.AppUserDetails;
import com.telerikacademy.com.springdemo.security.JwtUtil;
import com.telerikacademy.com.springdemo.services.RefreshTokenService;
import com.telerikacademy.com.springdemo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @Autowired
    public AuthController(UserService userService, RoleRepository roleRepository, PasswordEncoder passwordEncoder,
                          UserDetailsService userDetailsService, JwtUtil jwtUtil, RefreshTokenService refreshTokenService) {
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRegisterDto dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        Role userRole = roleRepository.getByName("USER");
        user.setRoles(Set.of(userRole));
        userService.create(user);

        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody UserRegisterDto dto) {
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(dto.getUsername());

            if (passwordEncoder.matches(dto.getPassword(), userDetails.getPassword())) {
                String accessToken = jwtUtil.generateToken(userDetails, 15 * 60 * 1000); // 15 mins

                RefreshToken refreshToken = refreshTokenService.createRefreshToken(((AppUserDetails) userDetails).getUser());

                ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken.getToken())
                        .httpOnly(true)
                        .secure(false)
                        .path("/auth/refresh")
                        .maxAge(7 * 24 * 60 * 60)
                        .build();

                return ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, cookie.toString())
                        .body(new LoginResponseDto(accessToken, refreshToken.getToken()));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@CookieValue(value = "refresh_token", required = false) String refreshToken,
                                         Authentication authentication) {
        if (refreshToken != null) {
            refreshTokenService.deleteByToken(refreshToken);
        }
        if (authentication != null && authentication.isAuthenticated()) {
            AppUserDetails userDetails = (AppUserDetails) authentication.getPrincipal();
            refreshTokenService.deleteAllByUser(userDetails.getUser());
        }
        return ResponseEntity.ok("Logged out successfully. Please discard your token.");
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@CookieValue(value = "refresh_token", required = false) String refreshToken) {

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token is missing");
        }

        return refreshTokenService.findByToken(refreshToken)
                .map(storedToken -> {
                    try {
                        refreshTokenService.verifyExpiration(storedToken);
                    } catch (Exception e) {
                        refreshTokenService.deleteByToken(refreshToken);
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Expired refresh token"));
                    }

                    User user = storedToken.getUser();
                    refreshTokenService.deleteByToken(refreshToken);
                    RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);
                    String newAccessToken = jwtUtil.generateToken(userDetailsService.loadUserByUsername(user.getUsername()), 15 * 60 * 1000);

                    ResponseCookie cookie = ResponseCookie.from("refresh_token", newRefreshToken.getToken())
                            .httpOnly(true)
                            .secure(false)
                            .path("/auth/refresh")
                            .maxAge(7 * 24 * 60 * 60)
                            .build();

                    return ResponseEntity.ok()
                            .header(HttpHeaders.SET_COOKIE, cookie.toString())
                            .body(new LoginResponseDto(newAccessToken, newRefreshToken.getToken()));
                })
                .orElseGet(() -> {
                    // Reuse detection or tampering attempt
                    refreshTokenService.logPotentialReuse(refreshToken);
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid refresh token"));
                });
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }

        var user = (AppUserDetails) authentication.getPrincipal();
        return ResponseEntity.ok(Map.of(
                "username", user.getUsername(),
                "id", user.getUser().getUserId(),
                "roles", user.getAuthorities()
        ));
    }

    @GetMapping("/protected-endpoint")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok(Map.of("message", "Success — you're authenticated"));
    }
}
