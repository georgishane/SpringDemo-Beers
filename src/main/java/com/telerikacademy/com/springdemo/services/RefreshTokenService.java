package com.telerikacademy.com.springdemo.services;

import com.telerikacademy.com.springdemo.models.RefreshToken;
import com.telerikacademy.com.springdemo.models.User;
import com.telerikacademy.com.springdemo.repositories.RefreshTokenRepository;
import com.telerikacademy.com.springdemo.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import static org.springframework.data.repository.util.ClassUtils.ifPresent;

@Service
public class RefreshTokenService {

    @Value("${app.jwt.refreshExpirationMs}") // e.g. 7 days in ms
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    @Autowired
    public RefreshTokenService(RefreshTokenRepository repo, UserService userService, UserRepository userRepository) {
        this.refreshTokenRepository = repo;
        this.userService = userService;
        this.userRepository = userRepository;
    }
    @Transactional
    public RefreshToken createRefreshToken(User user) {

        refreshTokenRepository.findByUser(user).ifPresent(refreshTokenRepository::delete);
        refreshTokenRepository.flush(); // force delete execution

        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        return refreshTokenRepository.save(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token expired");
        }
        return token;
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }

    public RefreshToken createRefreshToken(Long userId) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(userRepository.findById(userId).orElseThrow());
        refreshToken.setExpiryDate(Instant.now().plusMillis(7 * 24 * 60 * 60 * 1000)); // 7 days
        refreshToken.setToken(UUID.randomUUID().toString());

        return refreshTokenRepository.save(refreshToken);
    }
    @Transactional
    public void deleteAllByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }

    public void logPotentialReuse(String token) {

        Logger.getLogger(RefreshTokenService.class.getName()).warning("Reused or invalid refresh token detected: " + token);
        // Optionally: Store in an audit table or trigger alert
    }
    @Transactional
    public void deleteByToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }
}