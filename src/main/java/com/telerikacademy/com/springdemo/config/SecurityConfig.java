package com.telerikacademy.com.springdemo.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.telerikacademy.com.springdemo.exceptions.ApiError;
import com.telerikacademy.com.springdemo.security.JwtAuthenticationFilter;
import com.telerikacademy.com.springdemo.services.AppUserDetailsService;
import com.telerikacademy.com.springdemo.services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Map;
import java.util.logging.Logger;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           UserDetailsService userDetailsService,
                                           JwtAuthenticationFilter jwtFilter) throws Exception {

        http.cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/api/beers/search/**").permitAll()
                        // Authenticated user endpoints
                        .requestMatchers("/api/users/me").authenticated()
                        .requestMatchers("/api/comments/**").authenticated()
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // Admin-only endpoints
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/beers/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/beers/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/beers/**").hasRole("ADMIN")
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                // Add JWT filter BEFORE the UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                // Exception handling
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) -> {

                            Logger.getLogger(SecurityConfig.class.getName()).info("AuthHandler triggered: " + e.getMessage());
                            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            res.setContentType("application/json");
                            ApiError error = new ApiError(
                                    HttpStatus.UNAUTHORIZED,
                                    e.getMessage() != null ? e.getMessage() : "Authentication required"
                            );
                            new ObjectMapper().writeValue(res.getOutputStream(), error);
                        })
                        .accessDeniedHandler((req, res, e) -> {

                            Logger.getLogger(SecurityConfig.class.getName()).info("AccessDeniedHandler triggered: " + e.getMessage());
                            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            res.setContentType("application/json");
                            ApiError error = new ApiError(
                                    HttpStatus.FORBIDDEN,
                                    e.getMessage() != null ? e.getMessage() : "You don’t have permission to access this resource"
                            );
                            new ObjectMapper().writeValue(res.getOutputStream(), error);
                        })
                );

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserService userService) {
        return new AppUserDetailsService(userService);
    }

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}

