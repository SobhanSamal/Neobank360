package com.neobank.service;
 
import com.neobank.entity.User;

import com.neobank.repository.UserRepository;

import org.springframework.http.HttpStatus;

import org.springframework.security.core.Authentication;

import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Service;

import org.springframework.web.server.ResponseStatusException;
 
@Service

public class CurrentUserService {
 
    private final UserRepository userRepository;
 
    public CurrentUserService(UserRepository userRepository) {

        this.userRepository = userRepository;

    }
 
    public User getCurrentUser() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
 
        if (auth == null || !auth.isAuthenticated()) {

            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");

        }
 
        // JwtAuthenticationFilter sets username = email

        String email = auth.getName();

        if (email == null || email.isBlank()) {

            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authentication principal");

        }
 
        return userRepository.findByEmail(email)

                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

    }
 
    public void assertOwner(Long userId) {

        User me = getCurrentUser();

        if (userId == null || !userId.equals(me.getId())) {

            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");

        }

    }

}
 