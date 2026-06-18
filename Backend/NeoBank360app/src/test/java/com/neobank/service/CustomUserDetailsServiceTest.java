package com.neobank.service;


import com.neobank.entity.User;
import com.neobank.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomUserDetailsService Tests")
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setFullName("Test User");
        testUser.setPasswordHash("$2a$10$hashedPassword");
        testUser.setRole(User.Role.CUSTOMER);
        testUser.setIsActive(true);
    }

    @Test
    @DisplayName("Should load user by email successfully")
    void testLoadUserByUsernameSuccess() {

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(testUser));

        UserDetails userDetails =
                userDetailsService.loadUserByUsername("test@example.com");

        assertNotNull(userDetails);
        assertEquals("test@example.com", userDetails.getUsername());
        assertEquals("$2a$10$hashedPassword", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.isAccountNonLocked());

        verify(userRepository, times(1))
                .findByEmail("test@example.com");
    }

    @Test
    @DisplayName("Should set CUSTOMER authority for customer user")
    void testLoadUserWithCustomerRole() {

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(testUser));

        UserDetails userDetails =
                userDetailsService.loadUserByUsername("test@example.com");

        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth ->
                        auth.getAuthority().equals("ROLE_CUSTOMER")));
    }

    @Test
    @DisplayName("Should set ADMIN authority for admin user")
    void testLoadUserWithAdminRole() {

        testUser.setRole(User.Role.ADMIN);

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(testUser));

        UserDetails userDetails =
                userDetailsService.loadUserByUsername("test@example.com");

        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth ->
                        auth.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    @DisplayName("Should disable inactive user account")
    void testLoadInactiveUser() {

        testUser.setIsActive(false);

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(testUser));

        UserDetails userDetails =
                userDetailsService.loadUserByUsername("test@example.com");

        assertFalse(userDetails.isEnabled());
        assertFalse(userDetails.isAccountNonLocked());
    }

    @Test
    @DisplayName("Should lock account for inactive user")
    void testLoadInactiveUserAccountLocked() {

        testUser.setIsActive(false);

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(testUser));

        UserDetails userDetails =
                userDetailsService.loadUserByUsername("test@example.com");

        assertFalse(userDetails.isAccountNonLocked());
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user not found")
    void testLoadUserByUsernameNotFound() {

        when(userRepository.findByEmail("nonexistent@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService
                        .loadUserByUsername("nonexistent@example.com"));

        verify(userRepository, times(1))
                .findByEmail("nonexistent@example.com");
    }

    @Test
    @DisplayName("Should throw exception with specific message when user not found")
    void testLoadUserByUsernameNotFoundMessage() {

        when(userRepository.findByEmail("nonexistent@example.com"))
                .thenReturn(Optional.empty());

        UsernameNotFoundException exception =
                assertThrows(UsernameNotFoundException.class,
                        () -> userDetailsService
                                .loadUserByUsername("nonexistent@example.com"));

        assertTrue(exception.getMessage().contains("User not found"));
        assertTrue(exception.getMessage().contains("nonexistent@example.com"));
    }

    @Test
    @DisplayName("Should handle active user correctly")
    void testLoadActiveUser() {

        testUser.setIsActive(true);

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(testUser));

        UserDetails userDetails =
                userDetailsService.loadUserByUsername("test@example.com");

        assertTrue(userDetails.isEnabled());
    }

    @Test
    @DisplayName("Should preserve user password hash")
    void testPreservePasswordHash() {

        String expectedHash = "$2a$10$hashedPassword";
        testUser.setPasswordHash(expectedHash);

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(testUser));

        UserDetails userDetails =
                userDetailsService.loadUserByUsername("test@example.com");

        assertEquals(expectedHash, userDetails.getPassword());
    }

    @Test
    @DisplayName("Should load user with null IsActive field as disabled")
    void testLoadUserNullIsActive() {

        testUser.setIsActive(null);

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(testUser));

        UserDetails userDetails =
                userDetailsService.loadUserByUsername("test@example.com");

        assertFalse(userDetails.isEnabled());
    }

    @Test
    @DisplayName("Should have non-empty authorities list")
    void testUserHasAuthorities() {

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(testUser));

        UserDetails userDetails =
                userDetailsService.loadUserByUsername("test@example.com");

        assertFalse(userDetails.getAuthorities().isEmpty());
    }
}