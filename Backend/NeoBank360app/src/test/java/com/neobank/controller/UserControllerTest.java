package com.neobank.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.neobank.dto.UpdateProfileRequest;
import com.neobank.entity.User;
import com.neobank.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.Matchers.is;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserController Tests")
class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private User user;

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        user = new User();
        user.setId(1L);
        user.setEmail("customer@neobank.in");
        user.setFullName("Customer User");
        user.setRole(User.Role.CUSTOMER);
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should fetch authenticated user profile")
    void testGetProfile() throws Exception {

        when(authentication.getName()).thenReturn("customer@neobank.in");
        when(userRepository.findByEmail("customer@neobank.in"))
                .thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/me")
                .principal(authentication))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.email", is("customer@neobank.in")))
            .andExpect(jsonPath("$.fullName", is("Customer User")))
            .andExpect(jsonPath("$.role", is("CUSTOMER")))
            .andExpect(jsonPath("$.active", is(true)));

        verify(userRepository).findByEmail("customer@neobank.in");
    }

    @Test
    @DisplayName("Should update authenticated user profile")
    void testUpdateProfile() throws Exception {

        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFullName("Updated Customer");

        when(authentication.getName()).thenReturn("customer@neobank.in");
        when(userRepository.findByEmail("customer@neobank.in"))
                .thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        mockMvc.perform(put("/api/users/me")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.fullName", is("Updated Customer")));

        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Should fetch user by id for admin endpoint")
    void testGetUserById() throws Exception {

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email", is("customer@neobank.in")));

        verify(userRepository).findById(1L);
    }
}