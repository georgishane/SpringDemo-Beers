package com.telerikacademy.com.springdemo.services;

import com.telerikacademy.com.springdemo.models.RefreshToken;
import com.telerikacademy.com.springdemo.models.User;
import com.telerikacademy.com.springdemo.repositories.RefreshTokenRepository;
import com.telerikacademy.com.springdemo.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private User mockUser;
    private RefreshToken mockToken;

    @BeforeEach
    public void setup() {
        mockUser = new User();
        mockUser.setUserId(1);
        mockUser.setUsername("testuser");

        mockToken = new RefreshToken();
        mockToken.setId(1L);
        mockToken.setUser(mockUser);
        mockToken.setToken(UUID.randomUUID().toString());
        mockToken.setExpiryDate(Instant.now().plusSeconds(60));
    }

    @Test
    public void testCreateRefreshToken_ShouldReturnNewToken() {
        when(userRepository.findById(mockUser.getUserId())).thenReturn(Optional.of(mockUser));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(mockToken);

        RefreshToken created = refreshTokenService.createRefreshToken((long) mockUser.getUserId());

        assertNotNull(created);
        assertEquals(mockUser, created.getUser());
        assertNotNull(created.getToken());
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

        assertThrows(RuntimeException.class, () -> {
            refreshTokenService.verifyExpiration(mockToken);
        });

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
}

