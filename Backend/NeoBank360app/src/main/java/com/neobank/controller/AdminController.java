package com.neobank.controller;

import com.neobank.dto.UserResponse;
import com.neobank.dto.TransactionResponse;
import com.neobank.dto.UserActivityDTO;
import com.neobank.entity.User;
import com.neobank.repository.LoginEventRepository;
import com.neobank.repository.TransactionRepository;
import com.neobank.repository.UserRepository;
import com.neobank.service.AuditLogService;

import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminController.class);

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final LoginEventRepository loginEventRepository;
    private final AuditLogService auditLogService;

    public AdminController(
            UserRepository userRepository,
            TransactionRepository transactionRepository,
            LoginEventRepository loginEventRepository,
            AuditLogService auditLogService) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.loginEventRepository = loginEventRepository;
        this.auditLogService = auditLogService;
    }

    /* =========================
       USERS
    ========================= */
    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<UserResponse> users = userRepository.findAll(pageable)
                .map(UserResponse::fromEntity);

        return ResponseEntity.ok(users);
    }

    /* =========================
       UPDATE USER STATUS
    ========================= */
    @PatchMapping("/users/{userId}/status")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserResponse> updateUserStatus(
            @PathVariable Long userId,
            @RequestBody Map<String, Boolean> request,
            org.springframework.security.core.Authentication authentication) {

        User actingAdmin = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin not found"));

        Boolean active = request.get("isActive");
        if (active == null) {
            active = request.get("active");
        }
        if (active == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "isActive is required");
        }

        if (actingAdmin.getId().equals(userId) && !active) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Admin cannot deactivate own account");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        user.setIsActive(active);
        User saved = userRepository.save(user);

        LOGGER.info("AUDIT actingAdminId={} action=UPDATE_USER_STATUS targetResourceType=USER targetResourceId={} active={}",
                actingAdmin.getId(), userId, active);

        auditLogService.log(actingAdmin.getId(), "UPDATE_USER_STATUS", userId);

        return ResponseEntity.ok(UserResponse.fromEntity(saved));
    }

    /* =========================
       ✅ USER ACTIVITY (FIXED)
    ========================= */
    @GetMapping("/users/{userId}/activity")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Transactional(readOnly = true)   // ✅ ✅ ✅ FIX ADDED
    public ResponseEntity<UserActivityDTO> getUserActivity(@PathVariable Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // ✅ Transactions (lazy loading now works)
        List<TransactionResponse> transactions = transactionRepository
                .findTop20ByAccountUserIdOrderByTransactionDateDesc(user.getId())
                .stream()
                .map(TransactionResponse::from)
                .collect(Collectors.toList());

        // ✅ Login events
        List<String> loginEvents = loginEventRepository
                .findTop5ByUser_IdOrderByLoginAtDesc(user.getId())
                .stream()
                .map(event -> event.getLoginAt() + " | " +
                        (event.getIpAddress() != null ? event.getIpAddress() : "unknown ip"))
                .collect(Collectors.toList());

        return ResponseEntity.ok(new UserActivityDTO(user.getId(), transactions, loginEvents));
    }
}