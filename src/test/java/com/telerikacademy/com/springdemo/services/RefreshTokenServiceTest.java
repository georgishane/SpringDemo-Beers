package com.telerikacademy.com.springdemo.services;

import com.telerikacademy.com.springdemo.models.RefreshToken;
import com.telerikacademy.com.springdemo.models.User;
import com.telerikacademy.com.springdemo.repositories.RefreshTokenRepository;
import com.telerikacademy.com.springdemo.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceTest {

    private static final long USER_ID = 1L;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private User mockUser;
    private RefreshToken mockToken;

    @BeforeEach
    public void setUp() {
        mockUser = buildUser(USER_ID, "testuser");
        mockToken = buildToken(1L, mockUser, Instant.now().plusSeconds(60));
    }

    // ... existing code ...

    @Test
    public void testCreateRefreshToken_ShouldReturnNewToken() {
        when(userRepository.findById(mockUser.getUserId())).thenReturn(Optional.of(mockUser));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(mockToken);

        RefreshToken createdToken = refreshTokenService.createRefreshToken(mockUser.getUserId());

        assertNotNull(createdToken);
        assertEquals(mockUser, createdToken.getUser());
        assertNotNull(createdToken.getToken());
    }

    @Test
    public void testFindByToken_ShouldReturnTokenIfFound() {
        when(refreshTokenRepository.findByToken(mockToken.getToken())).thenReturn(Optional.of(mockToken));

        Optional<RefreshToken> found = refreshTokenService.findByToken(mockToken.getToken());

        assertTrue(found.isPresent());
        assertEquals(mockToken, found.get());
    }

    @Test
    public void testVerifyExpiration_ShouldReturnSameTokenIfNotExpired() {
        RefreshToken result = refreshTokenService.verifyExpiration(mockToken);
        assertEquals(mockToken, result);
    }

    @Test
    public void testVerifyExpiration_ShouldThrowIfExpired() {
        mockToken.setExpiryDate(Instant.now().minusSeconds(60)); // expired

        assertThrows(RuntimeException.class, () -> refreshTokenService.verifyExpiration(mockToken));
        verify(refreshTokenRepository).delete(mockToken);
    }

    @Test
    public void testDeleteByToken_ShouldCallRepository() {
        refreshTokenService.deleteByToken(mockToken.getToken());
        verify(refreshTokenRepository).deleteByToken(mockToken.getToken());
    }

    @Test
    public void testDeleteAllByUser_ShouldCallRepository() {
        refreshTokenService.deleteAllByUser(mockUser);
        verify(refreshTokenRepository).deleteByUser(mockUser);
    }

    // ... existing code ...

    private static User buildUser(long userId, String username) {
        User user = new User();
        user.setUserId(userId);
        user.setUsername(username);
        return user;
    }

    private static RefreshToken buildToken(long id, User user, Instant expiryDate) {
        RefreshToken token = new RefreshToken();
        token.setId(id);
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(expiryDate);
        return token; 
    }
}