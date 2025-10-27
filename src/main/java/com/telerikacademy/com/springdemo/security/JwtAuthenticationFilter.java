package com.telerikacademy.com.springdemo.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.telerikacademy.com.springdemo.exceptions.ApiError;
import com.telerikacademy.com.springdemo.exceptions.JwtAuthenticationException;
import com.telerikacademy.com.springdemo.models.User;
import com.telerikacademy.com.springdemo.services.AppUserDetailsService;
import com.telerikacademy.com.springdemo.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final AppUserDetailsService userDetailsService;
    private final UserService userService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, AppUserDetailsService userDetailsService, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                String username = jwtUtil.extractUsername(token);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    User user = userService.getByUserName(username);
                    AppUserDetails userDetails = new AppUserDetails(user);

                    var authorities = jwtUtil.extractRoles(token).stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

                    var authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, authorities);
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }

            } catch (io.jsonwebtoken.ExpiredJwtException e) {
                writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized", "Token expired");
                return;
            } catch (io.jsonwebtoken.JwtException e) {
                writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized", "Invalid token");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void writeErrorResponse(HttpServletResponse response, int status, String error, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        ApiError apiError = new ApiError(status, error, message);
        new ObjectMapper().writeValue(response.getOutputStream(), apiError);
    }
}
