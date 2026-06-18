package com.neobank.controller;

import com.neobank.dto.FinancialInsightsDTO;
import com.neobank.service.CurrentUserService;
import com.neobank.service.InsightsService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/insights")
@CrossOrigin(origins = "http://localhost:4200")
public class InsightsController {

    private final InsightsService insightsService;
    private final CurrentUserService currentUserService;

    public InsightsController(
            InsightsService insightsService,
            CurrentUserService currentUserService) {
        this.insightsService = insightsService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FinancialInsightsDTO> getInsights(
            @PathVariable Long userId) {

        // ✅ SECURITY CHECK
        currentUserService.assertOwner(userId);

        return ResponseEntity.ok(
                insightsService.buildInsights(userId)
        );
    }
}
