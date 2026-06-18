package com.neobank.service;

import com.neobank.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtService Tests")
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private User testUser;
    private UserDetails userDetails;
    private String jwtSecret;
    private long expirationMs;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setFullName("Test User");
        testUser.setRole(User.Role.CUSTOMER);

        userDetails = org.springframework.security.core.userdetails.User
            .withUsername("test@example.com")
            .password("password")
            .authorities(List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")))
            .build();

        jwtSecret = "NeoBank360JwtSecretKeyForDay3MustBeAtLeast32Chars";
        expirationMs = 86400000L; // 24 hours

        ReflectionTestUtils.setField(jwtService, "jwtSecret", jwtSecret);
        ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", expirationMs);
    }

    @Test
    @DisplayName("Should generate JWT token with correct claims")
    void testGenerateTokenSuccess() {
        // Act
        String token = jwtService.generateToken(testUser);

        // Assert
        assertNotNull(token);
        assertFalse(token.isBlank());
        assertTrue(token.contains("."));
        
        // Verify token contains expected parts (header.payload.signature)
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length);
    }

    @Test
    @DisplayName("Should extract email from token")
    void testExtractEmailSuccess() {
        // Arrange
        String token = jwtService.generateToken(testUser);

        // Act
        String email = jwtService.extractEmail(token);

        // Assert
        assertEquals("test@example.com", email);
    }

    @Test
    @DisplayName("Should validate token with correct user details")
    void testIsTokenValidSuccess() {
        // Arrange
        String token = jwtService.generateToken(testUser);

        // Act
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        // Assert
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Should invalidate token with mismatched user email")
    void testIsTokenValidMismatchedEmail() {
        // Arrange
        String token = jwtService.generateToken(testUser);
        UserDetails differentUser = org.springframework.security.core.userdetails.User
            .withUsername("different@example.com")
            .password("password")
            .authorities(List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")))
            .build();

        // Act
        boolean isValid = jwtService.isTokenValid(token, differentUser);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should contain user role in token claims")
    void testTokenContainsRoleClaim() {
        // Arrange
        String token = jwtService.generateToken(testUser);
        SecretKey signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        // Act
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(signingKey)
            .build()
            .parseClaimsJws(token)
            .getBody();

        // Assert
        assertEquals("CUSTOMER", claims.get("role"));
    }

    @Test
    @DisplayName("Should contain user ID in token claims")
    void testTokenContainsUserIdClaim() {
        // Arrange
        String token = jwtService.generateToken(testUser);
        SecretKey signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        // Act
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(signingKey)
            .build()
            .parseClaimsJws(token)
            .getBody();

        // Assert
        assertEquals(1, claims.get("userId"));
    }

    @Test
    @DisplayName("Should contain email as subject in token")
    void testTokenContainsEmailAsSubject() {
        // Arrange
        String token = jwtService.generateToken(testUser);
        SecretKey signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        // Act
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(signingKey)
            .build()
            .parseClaimsJws(token)
            .getBody();

        // Assert
        assertEquals("test@example.com", claims.getSubject());
    }

    @Test
    @DisplayName("Should reject expired token")
    void testIsTokenValidExpired() {
        // Arrange - set very short expiration
        ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", 1L);
        String token = jwtService.generateToken(testUser);
        
        // Sleep to ensure token expires
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Reset expiration for other tests
        ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", expirationMs);

        // Act
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should generate different tokens for multiple calls")
    void testGenerateTokenUniqueness() {
        // Act
        String token1 = jwtService.generateToken(testUser);
        String token2 = jwtService.generateToken(testUser);

        // Assert
        assertNotEquals(token1, token2);
    }

    @Test
    @DisplayName("Should handle admin user role in token")
    void testGenerateTokenAdminRole() {
        // Arrange
        testUser.setRole(User.Role.ADMIN);
        SecretKey signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        // Act
        String token = jwtService.generateToken(testUser);

        // Assert
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(signingKey)
            .build()
            .parseClaimsJws(token)
            .getBody();
        assertEquals("ADMIN", claims.get("role"));
    }

    @Test
    @DisplayName("Should validate token structure")
    void testTokenStructureValidity() {
        // Arrange
        String token = jwtService.generateToken(testUser);

        // Act & Assert
        assertDoesNotThrow(() -> {
            SecretKey signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token);
        });
    }

    @Test
    @DisplayName("Should reject malformed token")
    void testIsTokenValidMalformed() {
        // Arrange
        String malformedToken = "invalid.token.here";
        
        // Act & Assert
        assertThrows(Exception.class, () -> jwtService.isTokenValid(malformedToken, userDetails));
    }
}
