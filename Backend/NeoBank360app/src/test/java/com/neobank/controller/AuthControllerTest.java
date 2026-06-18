package com.neobank.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.http.MediaType;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.neobank.dto.RegisterRequest;
import com.neobank.dto.UserResponse;
import com.neobank.service.UserService;

import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testRegisterSuccessfully() throws Exception {

        // Arrange
        RegisterRequest request = new RegisterRequest(
                "John Doe",
                "john@neobank.in",
                "SecurePass@123",
                "SecurePass@123"
        );

        UserResponse response = new UserResponse(
                1L,
                "john@neobank.in",
                "John Doe",
                "CUSTOMER",
                true,
                LocalDateTime.now()
        );

        when(userService.register(any(RegisterRequest.class)))
                .thenReturn(response);

        // Act + Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.email", is("john@neobank.in")))
            .andExpect(jsonPath("$.fullName", is("John Doe")))
            .andExpect(jsonPath("$.role", is("CUSTOMER")))
            .andExpect(jsonPath("$.active", is(true)));

        verify(userService, times(1)).register(any(RegisterRequest.class));
    }

    @Test
    void testRegisterWithDuplicateEmail() throws Exception {

        // Arrange
        RegisterRequest request = new RegisterRequest(
                "John Doe",
                "john@neobank.in",
                "SecurePass@123",
                "SecurePass@123"
        );

        when(userService.register(any(RegisterRequest.class)))
                .thenThrow(new IllegalArgumentException("Email already registered"));

        // Act + Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message", is("Email already registered")))
            .andExpect(jsonPath("$.status", is(409)));

        verify(userService, times(1)).register(any(RegisterRequest.class));
    }

    @Test
    void testRegisterWithPasswordMismatch() throws Exception {

        // Arrange
        RegisterRequest request = new RegisterRequest(
                "John Doe",
                "john@neobank.in",
                "SecurePass@123",
                "DifferentPass@456"
        );

        when(userService.register(any(RegisterRequest.class)))
                .thenThrow(new IllegalArgumentException("Passwords do not match"));

        // Act + Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", is("Passwords do not match")))
            .andExpect(jsonPath("$.status", is(400)));

        verify(userService, times(1)).register(any(RegisterRequest.class));
    }

    @Test
    void testRegisterWithInvalidEmail() throws Exception {

        // Arrange
        RegisterRequest request = new RegisterRequest(
                "John Doe",
                "invalid-email",
                "SecurePass@123",
                "SecurePass@123"
        );

        // Act + Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.email", notNullValue()));

        verify(userService, never()).register(any());
    }

    @Test
    void testRegisterWithWeakPassword() throws Exception {

        // Arrange
        RegisterRequest request = new RegisterRequest(
                "John Doe",
                "john@neobank.in",
                "weak",
                "weak"
        );

        // Act + Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.password", notNullValue()));

        verify(userService, never()).register(any());
    }

    @Test
    void testRegisterWithMissingFullName() throws Exception {

        // Arrange
        RegisterRequest request = new RegisterRequest(
                "",
                "john@neobank.in",
                "SecurePass@123",
                "SecurePass@123"
        );

        // Act + Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fullName", notNullValue()));

        verify(userService, never()).register(any());
    }

    @Test
    void testRegisterWithMissingEmail() throws Exception {

        // Arrange
        RegisterRequest request = new RegisterRequest(
                "John Doe",
                "",
                "SecurePass@123",
                "SecurePass@123"
        );

        // Act + Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.email", notNullValue()));

        verify(userService, never()).register(any());
    }

    @Test
    void testRegisterWithMissingPassword() throws Exception {

        // Arrange
        RegisterRequest request = new RegisterRequest(
                "John Doe",
                "john@neobank.in",
                "",
                ""
        );

        // Act + Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.password", notNullValue()));

        verify(userService, never()).register(any());
    }
}