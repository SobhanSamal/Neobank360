package com.neobank.controller;

import com.neobank.dto.UserResponse;
import com.neobank.dto.UpdateProfileRequest;
import com.neobank.entity.User;
import com.neobank.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserRepository userRepository;

  public UserController(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @GetMapping("/me")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<UserResponse> getProfile(Authentication auth) {
    String email = auth.getName();
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));
    return ResponseEntity.ok(UserResponse.fromEntity(user));
  }

  @PutMapping("/me")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<UserResponse> updateProfile(
      Authentication auth,
      @Valid @RequestBody UpdateProfileRequest request) {
    String email = auth.getName();
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));

    if (request.getFullName() != null && !request.getFullName().isBlank()) {
      user.setFullName(request.getFullName());
    }

    User updated = userRepository.save(user);
    return ResponseEntity.ok(UserResponse.fromEntity(updated));
  }

  @GetMapping("/{userId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));
    return ResponseEntity.ok(UserResponse.fromEntity(user));
  }
}
