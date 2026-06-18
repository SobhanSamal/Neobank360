package com.neobank.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RegisterRequestValidationTest {

    private Validator validator;
    private RegisterRequest validRequest;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        validRequest = new RegisterRequest(
                "John Doe",
                "john@neobank.in",
                "SecurePass@123",
                "SecurePass@123"
        );
    }

    @Test
    void testValidRegistrationRequest() {

        Set<ConstraintViolation<RegisterRequest>> violations =
                validator.validate(validRequest);

        assertTrue(violations.isEmpty());
    }

    @Test
    void testBlankFullName() {

        validRequest.setFullName("");

        Set<ConstraintViolation<RegisterRequest>> violations =
                validator.validate(validRequest);

        assertFalse(violations.isEmpty());

        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath()
                        .toString().equals("fullName")));
    }

    @Test
    void testShortFullName() {

        validRequest.setFullName("Jo");

        Set<ConstraintViolation<RegisterRequest>> violations =
                validator.validate(validRequest);

        assertFalse(violations.isEmpty());

        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath()
                        .toString().equals("fullName")));
    }

    @Test
    void testInvalidEmail() {

        validRequest.setEmail("invalid-email");

        Set<ConstraintViolation<RegisterRequest>> violations =
                validator.validate(validRequest);

        assertFalse(violations.isEmpty());

        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath()
                        .toString().equals("email")));
    }

    @Test
    void testBlankEmail() {

        validRequest.setEmail("");

        Set<ConstraintViolation<RegisterRequest>> violations =
                validator.validate(validRequest);

        assertFalse(violations.isEmpty());

        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath()
                        .toString().equals("email")));
    }

    @Test
    void testShortPassword() {

        validRequest.setPassword("Short@1");

        Set<ConstraintViolation<RegisterRequest>> violations =
                validator.validate(validRequest);

        assertFalse(violations.isEmpty());

        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath()
                        .toString().equals("password")));
    }

    @Test
    void testPasswordWithoutUppercase() {

        validRequest.setPassword("lowercase@123");

        Set<ConstraintViolation<RegisterRequest>> violations =
                validator.validate(validRequest);

        assertFalse(violations.isEmpty());

        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath()
                        .toString().equals("password")));
    }

    @Test
    void testPasswordWithoutLowercase() {

        validRequest.setPassword("UPPERCASE@123");

        Set<ConstraintViolation<RegisterRequest>> violations =
                validator.validate(validRequest);

        assertFalse(violations.isEmpty());

        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath()
                        .toString().equals("password")));
    }

    @Test
    void testPasswordWithoutDigit() {

        validRequest.setPassword("NoDigits@here");

        Set<ConstraintViolation<RegisterRequest>> violations =
                validator.validate(validRequest);

        assertFalse(violations.isEmpty());

        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath()
                        .toString().equals("password")));
    }

    @Test
    void testPasswordWithoutSpecialChar() {

        validRequest.setPassword("NoSpecial123");

        Set<ConstraintViolation<RegisterRequest>> violations =
                validator.validate(validRequest);

        assertFalse(violations.isEmpty());

        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath()
                        .toString().equals("password")));
    }

    @Test
    void testBlankPassword() {

        validRequest.setPassword("");

        Set<ConstraintViolation<RegisterRequest>> violations =
                validator.validate(validRequest);

        assertFalse(violations.isEmpty());

        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath()
                        .toString().equals("password")));
    }

    @Test
    void testBlankConfirmPassword() {

        validRequest.setConfirmPassword("");

        Set<ConstraintViolation<RegisterRequest>> violations =
                validator.validate(validRequest);

        assertFalse(violations.isEmpty());

        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath()
                        .toString().equals("confirmPassword")));
    }

    @Test
    void testPasswordWithSpecialCharHash() {

        validRequest.setPassword("Secure@Pass#123");
        validRequest.setConfirmPassword("Secure@Pass#123");

        Set<ConstraintViolation<RegisterRequest>> violations =
                validator.validate(validRequest);

        assertTrue(violations.isEmpty());
    }

    @Test
    void testPasswordWithSpecialCharParen() {

        validRequest.setPassword("Secure(Pass)123");
        validRequest.setConfirmPassword("Secure(Pass)123");

        Set<ConstraintViolation<RegisterRequest>> violations =
                validator.validate(validRequest);

        assertTrue(violations.isEmpty());
    }
}
