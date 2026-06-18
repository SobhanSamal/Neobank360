package com.neobank.config;

import com.neobank.repository.UserRepository;
import com.neobank.security.JwtAuthenticationFilter;
import com.neobank.service.JwtService;

/**
 * Compatibility wrapper to match requested project structure.
 * Current active filter implementation remains in JwtAuthenticationFilter.
 */
public class JwtAuthFilter extends JwtAuthenticationFilter {

    public JwtAuthFilter(JwtService jwtService, UserRepository userRepository) {
        super(jwtService, userRepository);
    }
}
