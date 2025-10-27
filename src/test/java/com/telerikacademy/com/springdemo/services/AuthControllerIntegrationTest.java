package com.telerikacademy.com.springdemo.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.telerikacademy.com.springdemo.models.UserRegisterDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockCookie;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String username;
    private String password = "pass123";
    private String refreshToken;

    @BeforeEach
    public void setup() {
        username = "user" + System.currentTimeMillis();
    }

    @Test
    public void testRegisterAndLoginAndRefresh() throws Exception {
        // Register
        UserRegisterDto registerDto = new UserRegisterDto();
        registerDto.setUsername(username);
        registerDto.setPassword(password);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isCreated())
                .andExpect(content().string("User registered successfully"));

        // Login
        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(cookie().exists("refresh_token"))
                .andReturn();

        String responseJson = loginResult.getResponse().getContentAsString();
        Map<String, String> tokens = objectMapper.readValue(responseJson, Map.class);
        refreshToken = tokens.get("refreshToken");

        // Refresh
        mockMvc.perform(post("/auth/refresh")
                        .cookie(new MockCookie("refresh_token", refreshToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    public void testMeEndpoint_Unauthenticated_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testRefresh_WithInvalidToken_ShouldReturn401() throws Exception {
        mockMvc.perform(post("/auth/refresh")
                        .cookie(new MockCookie("refresh_token", "invalidtoken")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testLogout_ShouldSucceedEvenWithoutToken() throws Exception {
        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(content().string("Logged out successfully. Please discard your token."));
    }
}
