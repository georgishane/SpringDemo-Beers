package com.telerikacademy.com.springdemo.repositories;

import com.telerikacademy.com.springdemo.models.RefreshToken;
import com.telerikacademy.com.springdemo.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(User user);
}
