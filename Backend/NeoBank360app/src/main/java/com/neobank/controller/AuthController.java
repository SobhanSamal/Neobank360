package com.neobank.controller;
 
import com.neobank.dto.LoginRequest;
import com.neobank.dto.LoginResponse;
import com.neobank.dto.RegisterRequest;
import com.neobank.dto.UserResponse;
import com.neobank.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
 
import java.util.HashMap;
import java.util.Map;
 
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {
 
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
 
    private final UserService userService;
 
    public AuthController(UserService userService) {
        this.userService = userService;
    }
 
    /**
     * Register a new user
     * POST /api/auth/register
     * @param request RegisterRequest with fullName, email, password, confirmPassword
     * @return 201 Created with UserResponse, or 400/409 on validation error or duplicate email
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            log.info("Registration request for email: {}", request.getEmail());
 
            UserResponse userResponse = userService.register(request);
 
            log.info("User registered successfully: {}", userResponse.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
 
        } catch (IllegalArgumentException e) {
            log.warn("Registration validation failed: {}", e.getMessage());
 
            if (e.getMessage().contains("already registered")) {
                return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("Email already registered", 409));
            } else if (e.getMessage().contains("do not match")) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Passwords do not match", 400));
            }
 
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage(), 400));
 
        } catch (Exception e) {
            log.error("Unexpected error during registration", e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Registration failed: " + e.getMessage(), 500));
        }
    }
 
    /**
     * Authenticate user and return JWT
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        try {
            LoginResponse loginResponse = userService.login(request, httpRequest);
            return ResponseEntity.ok(loginResponse);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("inactive")) {
                return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("User is inactive", 403));
            }
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("Invalid credentials", 401));
        }
    }
 
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }
 
    /**
     * Error response DTO
     */
    public static class ErrorResponse {
        private String message;
        private int status;
 
        public ErrorResponse() {
        }
 
        public ErrorResponse(String message, int status) {
            this.message = message;
            this.status = status;
        }
 
        public String getMessage() {
            return message;
        }
 
        public void setMessage(String message) {
            this.message = message;
        }
 
        public int getStatus() {
            return status;
        }
 
        public void setStatus(int status) {
            this.status = status;
        }
    }
}
 