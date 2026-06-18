package com.neobank.service;
 
import com.neobank.dto.LoginRequest;
import com.neobank.dto.LoginResponse;
import com.neobank.dto.RegisterRequest;
import com.neobank.dto.UserResponse;
import com.neobank.entity.LoginEvent;
import com.neobank.entity.User;
import com.neobank.repository.LoginEventRepository;
import com.neobank.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
import jakarta.servlet.http.HttpServletRequest;
 
@Service
public class UserService {
 
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
 
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final LoginEventRepository loginEventRepository;
 
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, JwtService jwtService, LoginEventRepository loginEventRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.loginEventRepository = loginEventRepository;
    }
 
    /**
     * Register a new user with email uniqueness validation and password hashing
     * @param request RegisterRequest containing user details
     * @return UserResponse with created user details (password excluded)
     * @throws IllegalArgumentException if email already exists or passwords don't match
     */
    @Transactional
    public UserResponse register(RegisterRequest request) {
        // Validate passwords match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            log.warn("Password mismatch for email: {}", request.getEmail());
            throw new IllegalArgumentException("Passwords do not match");
        }
 
        // Check email uniqueness
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration attempt with duplicate email: {}", request.getEmail());
            throw new IllegalArgumentException("Email already registered");
        }
 
        // Hash password and create user
        String passwordHash = passwordEncoder.encode(request.getPassword());
 
        User user = new User();
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPasswordHash(passwordHash);
        user.setRole(User.Role.CUSTOMER);
        user.setIsActive(true);
 
        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getEmail());
 
        return UserResponse.fromEntity(savedUser);
    }
 
    /**
     * Find user by email (for authentication)
     * @param email User email
     * @return User entity if found
     */
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
 
    /**
     * Login user and issue JWT token
     * @param request LoginRequest with email and password
     * @return LoginResponse containing JWT token
     */
    public LoginResponse login(LoginRequest request) {
        return login(request, null);
    }
 
    @Transactional
    public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
 
        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new IllegalArgumentException("User is inactive");
        }
 
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
 
        LoginEvent event = new LoginEvent();
        event.setUser(user);
        if (httpRequest != null) {
            event.setIpAddress(httpRequest.getRemoteAddr());
            event.setUserAgent(httpRequest.getHeader("User-Agent"));
        }
        loginEventRepository.save(event);
 
        String token = jwtService.generateToken(user);
        return new LoginResponse(token, "Bearer", user.getEmail(), user.getRole().name());
    }
}
 