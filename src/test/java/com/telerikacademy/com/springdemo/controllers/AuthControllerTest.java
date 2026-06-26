package com.telerikacademy.com.springdemo.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.telerikacademy.com.springdemo.config.SecurityConfig;
import com.telerikacademy.com.springdemo.dto.request.UserRegisterDto;
import com.telerikacademy.com.springdemo.models.RefreshToken;
import com.telerikacademy.com.springdemo.models.Role;
import com.telerikacademy.com.springdemo.models.User;
import com.telerikacademy.com.springdemo.repositories.*;
import com.telerikacademy.com.springdemo.security.AppUserDetails;

import com.telerikacademy.com.springdemo.security.JwtUtil;
import com.telerikacademy.com.springdemo.services.RefreshTokenService;
import com.telerikacademy.com.springdemo.services.UserService;

import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Set;

import static org.hamcrest.Matchers.containsString;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = AuthController.class)

class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // =========================
    // MOCKED DEPENDENCIES
    // =========================

    @MockBean
    private UserService userService;

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private RefreshTokenService refreshTokenService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    // =====================================================
    // REGISTER TEST
    // =====================================================

    @Test
    void register_returns201() throws Exception {

        Role role = new Role();
        role.setName("USER");

        Mockito.when(roleRepository.getByName("USER"))
                .thenReturn(role);

        UserRegisterDto dto = new UserRegisterDto();
        dto.setUsername("testUser");
        dto.setPassword("12345");

        mockMvc.perform(post("/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().string("User registered successfully"));
    }


    // =====================================================
    // LOGIN SUCCESS TEST
    // =====================================================

    @Test
    void login_returns200_whenCredentialsAreValid() throws Exception {

        User user = new User();
        user.setUsername("john");

        Role role = new Role();
        role.setName("USER");

        user.setRoles(Set.of(role));

        AppUserDetails userDetails = Mockito.mock(AppUserDetails.class);

        Mockito.when(userDetails.getUser())
                .thenReturn(user);

        Mockito.when(userDetails.getUsername())
                .thenReturn("john");

        Mockito.when(userDetails.getPassword())
                .thenReturn("encoded-password");

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("mock-refresh-token-123");
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(
                Instant.now().plusSeconds(604800)
        );

        UserRegisterDto dto = new UserRegisterDto();
        dto.setUsername("john");
        dto.setPassword("1234");

        Mockito.when(userDetailsService.loadUserByUsername("john"))
                .thenReturn(userDetails);

        Mockito.when(passwordEncoder.matches(
                        "1234",
                        "encoded-password"))
                .thenReturn(true);

        Mockito.when(jwtUtil.generateToken(any(), anyLong()))
                .thenReturn("mock-access-token-xyz");

        Mockito.when(refreshTokenService.createRefreshToken(user))
                .thenReturn(refreshToken);

        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.accessToken")
                        .value("mock-access-token-xyz"))

                .andExpect(jsonPath("$.refreshToken")
                        .value("mock-refresh-token-123"))

                .andExpect(header().string(
                        HttpHeaders.SET_COOKIE,
                        containsString("refresh_token")
                ));
    }


    // =====================================================
    // LOGIN INVALID PASSWORD
    // =====================================================

    @Test
    void login_returns401_whenPasswordIsInvalid() throws Exception {

        AppUserDetails userDetails = Mockito.mock(AppUserDetails.class);

        Mockito.when(userDetails.getPassword())
                .thenReturn("encoded-password");

        UserRegisterDto dto = new UserRegisterDto();
        dto.setUsername("john");
        dto.setPassword("wrong-password");

        Mockito.when(userDetailsService.loadUserByUsername("john"))
                .thenReturn(userDetails);

        Mockito.when(passwordEncoder.matches(
                        "wrong-password",
                        "encoded-password"))
                .thenReturn(false);

        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }


    // =====================================================
    // LOGIN USER NOT FOUND
    // =====================================================

    @Test
    void login_returns401_whenUserDoesNotExist() throws Exception {

        UserRegisterDto dto = new UserRegisterDto();
        dto.setUsername("missing-user");
        dto.setPassword("1234");

        Mockito.when(userDetailsService
                        .loadUserByUsername("missing-user"))
                .thenThrow(
                        new org.springframework.security.core.userdetails
                                .UsernameNotFoundException("User not found")
                );

        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }
}