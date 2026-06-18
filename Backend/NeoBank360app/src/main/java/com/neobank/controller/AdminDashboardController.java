package com.neobank.controller;

import com.neobank.dto.AdminDashboardDTO;
import com.neobank.dto.PendingApprovalDTO;
import com.neobank.dto.SystemHealthDTO;
import com.neobank.service.AdminDashboardService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminDashboardController {

    private final AdminDashboardService service;

    public AdminDashboardController(AdminDashboardService service) {
        this.service = service;
    }

    /* =========================
       DASHBOARD
    ========================= */
    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardDTO> dashboard() {
        return ResponseEntity.ok(service.buildDashboard());
    }

    /* =========================
       PENDING APPROVALS
    ========================= */
    @GetMapping("/pending-approvals")
    public ResponseEntity<List<PendingApprovalDTO>> pending() {
        return ResponseEntity.ok(service.getPendingApprovals());
    }

    /* =========================
       SYSTEM HEALTH ✅
    ========================= */
    @GetMapping("/system-health")
    public ResponseEntity<SystemHealthDTO> systemHealth() {
        return ResponseEntity.ok(service.getSystemHealth());
    }
}