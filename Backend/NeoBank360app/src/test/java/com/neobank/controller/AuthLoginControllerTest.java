package com.neobank.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neobank.dto.LoginRequest;
import com.neobank.dto.LoginResponse;
import com.neobank.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController Login Tests")
class AuthLoginControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(authController)
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Should login successfully and return JWT response")
    void testLoginSuccess() throws Exception {

        LoginRequest request =
                new LoginRequest("customer@neobank.in", "Customer@123");

        LoginResponse response = new LoginResponse(
                "jwt-token",
                "Bearer",
                "customer@neobank.in",
                "CUSTOMER"
        );

        when(userService.login(any(LoginRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token", is("jwt-token")))
            .andExpect(jsonPath("$.tokenType", is("Bearer")))
            .andExpect(jsonPath("$.email", is("customer@neobank.in")))
            .andExpect(jsonPath("$.role", is("CUSTOMER")));

        verify(userService).login(any(LoginRequest.class));
    }

    @Test
    @DisplayName("Should return 401 for invalid credentials")
    void testLoginInvalidCredentials() throws Exception {

        LoginRequest request =
                new LoginRequest("customer@neobank.in", "Wrong@123");

        when(userService.login(any(LoginRequest.class)))
                .thenThrow(new IllegalArgumentException("Invalid credentials"));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message", is("Invalid credentials")))
            .andExpect(jsonPath("$.status", is(401)));

        verify(userService).login(any(LoginRequest.class));
    }

    @Test
    @DisplayName("Should return 403 for inactive user login")
    void testLoginInactiveUser() throws Exception {

        LoginRequest request =
                new LoginRequest("inactive@neobank.in", "Inactive@123");

        when(userService.login(any(LoginRequest.class)))
                .thenThrow(new IllegalArgumentException("User is inactive"));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.message", is("User is inactive")))
            .andExpect(jsonPath("$.status", is(403)));

        verify(userService).login(any(LoginRequest.class));
    }

    @Test
    @DisplayName("Should reject invalid login payload")
    void testLoginValidationFailure() throws Exception {

        LoginRequest request =
                new LoginRequest("not-an-email", "");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.email").exists())
            .andExpect(jsonPath("$.password").exists());

        verify(userService, never()).login(any(LoginRequest.class));
    }
}
