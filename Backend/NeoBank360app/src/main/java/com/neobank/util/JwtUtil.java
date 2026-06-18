package com.neobank.util;

import com.neobank.entity.User;
import com.neobank.service.JwtService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    private final JwtService jwtService;

    public JwtUtil(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public String generateToken(User user) {
        return jwtService.generateToken(user);
    }

    public String extractUsername(String token) {
        return jwtService.extractEmail(token);
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        return jwtService.isTokenValid(token, userDetails);
    }
}
