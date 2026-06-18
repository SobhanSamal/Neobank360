package com.neobank.service;
 
import com.neobank.dto.LoginRequest;
import com.neobank.dto.LoginResponse;
import com.neobank.dto.RegisterRequest;
import com.neobank.dto.UserResponse;
import com.neobank.entity.User;
import com.neobank.repository.LoginEventRepository;
import com.neobank.repository.UserRepository;
 
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
 
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
 
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
 
import java.util.Optional;
 
import static org.junit.jupiter.api.Assertions.*;
 
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
 
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {
 
    @Mock
    private UserRepository userRepository;
 
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
 
    @Mock
    private JwtService jwtService;
 
    @Mock
    private LoginEventRepository loginEventRepository;
 
    @InjectMocks
    private UserService userService;
 
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User testUser;
 
    @BeforeEach
    void setUp() {
 
        registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setFullName("Test User");
        registerRequest.setPassword("password123");
        registerRequest.setConfirmPassword("password123");
 
        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");
 
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setFullName("Test User");
        testUser.setPasswordHash("$2a$10$hashedPassword");
        testUser.setRole(User.Role.CUSTOMER);
        testUser.setIsActive(true);
    }
 
    @Test
    @DisplayName("Should register user successfully with matching passwords")
    void testRegisterSuccess() {
 
        when(userRepository.existsByEmail(registerRequest.getEmail()))
                .thenReturn(false);
 
        when(passwordEncoder.encode(registerRequest.getPassword()))
                .thenReturn("hashedPassword");
 
        when(userRepository.save(any(User.class)))
                .thenReturn(testUser);
 
        UserResponse response =
                userService.register(registerRequest);
 
        assertNotNull(response);
        assertEquals("test@example.com", response.getEmail());
        assertEquals("Test User", response.getFullName());
 
        verify(userRepository, times(1))
                .save(any(User.class));
    }
 
    @Test
    @DisplayName("Should throw exception when passwords don't match")
    void testRegisterPasswordMismatch() {
 
        registerRequest.setConfirmPassword("differentPassword");
 
        assertThrows(IllegalArgumentException.class,
                () -> userService.register(registerRequest));
 
        verify(userRepository, never())
                .save(any(User.class));
    }
 
    @Test
    @DisplayName("Should throw exception when email already exists")
    void testRegisterDuplicateEmail() {
 
        when(userRepository.existsByEmail(registerRequest.getEmail()))
                .thenReturn(true);
 
        assertThrows(IllegalArgumentException.class,
                () -> userService.register(registerRequest));
 
        verify(userRepository, never())
                .save(any(User.class));
    }
 
    @Test
    @DisplayName("Should find user by email successfully")
    void testFindByEmailSuccess() {
 
        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(testUser));
 
        User foundUser =
                userService.findByEmail("test@example.com");
 
        assertNotNull(foundUser);
        assertEquals("test@example.com", foundUser.getEmail());
 
        verify(userRepository, times(1))
                .findByEmail("test@example.com");
    }
 
    @Test
    @DisplayName("Should throw exception when user not found")
    void testFindByEmailNotFound() {
 
        when(userRepository.findByEmail("nonexistent@example.com"))
                .thenReturn(Optional.empty());
 
        assertThrows(IllegalArgumentException.class,
                () -> userService.findByEmail("nonexistent@example.com"));
    }
 
    @Test
    @DisplayName("Should login user successfully with valid credentials")
    void testLoginSuccess() {
 
        when(userRepository.findByEmail(loginRequest.getEmail()))
                .thenReturn(Optional.of(testUser));
 
        when(passwordEncoder.matches(
                loginRequest.getPassword(),
                testUser.getPasswordHash()))
            .thenReturn(true);
 
        when(jwtService.generateToken(testUser))
                .thenReturn("jwt_token_123");
        when(loginEventRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));
 
        LoginResponse response =
                userService.login(loginRequest);
 
        assertNotNull(response);
        assertEquals("jwt_token_123", response.getToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals("test@example.com", response.getEmail());
        assertEquals("CUSTOMER", response.getRole());
 
        verify(jwtService, times(1))
                .generateToken(testUser);
    }
 
    @Test
    @DisplayName("Should throw exception when user doesn't exist during login")
    void testLoginUserNotFound() {
 
        when(userRepository.findByEmail(loginRequest.getEmail()))
                .thenReturn(Optional.empty());
 
        assertThrows(IllegalArgumentException.class,
                () -> userService.login(loginRequest));
    }
 
    @Test
    @DisplayName("Should throw exception when user is inactive")
    void testLoginInactiveUser() {
 
        testUser.setIsActive(false);
 
        when(userRepository.findByEmail(loginRequest.getEmail()))
                .thenReturn(Optional.of(testUser));
 
        assertThrows(IllegalArgumentException.class,
                () -> userService.login(loginRequest));
    }
 
    @Test
    @DisplayName("Should throw exception when password is incorrect")
    void testLoginInvalidPassword() {
 
        when(userRepository.findByEmail(loginRequest.getEmail()))
                .thenReturn(Optional.of(testUser));
 
        when(passwordEncoder.matches(
                loginRequest.getPassword(),
                testUser.getPasswordHash()))
            .thenReturn(false);
 
        assertThrows(IllegalArgumentException.class,
                () -> userService.login(loginRequest));
    }
}
 