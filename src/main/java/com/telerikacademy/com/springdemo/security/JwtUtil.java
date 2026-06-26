package com.telerikacademy.com.springdemo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    private static final String SECRET_KEY = "mysecretkeymysecretkeymysecretkey123!"; // at least 32 chars

    private final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    // Generate JWT token with username and roles claims
    public String generateToken(UserDetails userDetails, long expirationMillis) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("roles", userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(key)
                .compact();
    }

    // Extract username (subject) from JWT token
    public String extractUsername(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    // Validate the JWT token (checks signature & expiration)
    public boolean isValid(String token) {
        try {
            getClaimsFromToken(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    // Extract all claims (payload data) from the token
    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Optional helper: extract role list from token
    public List<String> extractRoles(String token) {
        Object rolesClaim = getClaimsFromToken(token).get("roles");
        if (!(rolesClaim instanceof List<?> roles)) {
            throw new JwtException("Invalid roles claim");
        }

        return roles.stream()
                .map(role -> {
                    if (!(role instanceof String)) {
                        throw new JwtException("Invalid role value in token");
                    }
                    return (String) role;
                })
                .collect(Collectors.toList());
    }
}
