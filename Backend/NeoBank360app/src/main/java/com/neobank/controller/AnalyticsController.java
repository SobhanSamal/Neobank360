package com.neobank.controller;
 
import com.neobank.dto.AdminLoanAnalyticsDTO;
import com.neobank.dto.AdminTransactionAnalyticsDTO;
import com.neobank.dto.SystemAuditLogDTO;
import com.neobank.dto.UserSpendingAnalyticsDTO;
import com.neobank.dto.UserWealthAnalyticsDTO;
import com.neobank.service.AnalyticsService;
import com.neobank.service.CurrentUserService;
 
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
 
import java.time.LocalDateTime;
 
@RestController
public class AnalyticsController {
    private final AnalyticsService analyticsService;
    private final CurrentUserService currentUserService;
 
    public AnalyticsController(AnalyticsService analyticsService, CurrentUserService currentUserService) {
        this.analyticsService = analyticsService;
        this.currentUserService = currentUserService;
    }
 
    @GetMapping("/api/admin/analytics/transactions")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<AdminTransactionAnalyticsDTO> transactions(@RequestParam String timeframe) {
        return ResponseEntity.ok(analyticsService.getAdminTransactionAnalytics(timeframe));
    }
 
    @GetMapping("/api/admin/analytics/loans")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<AdminLoanAnalyticsDTO> loans() {
        return ResponseEntity.ok(analyticsService.getAdminLoanAnalytics());
    }
 
    @GetMapping("/api/admin/system-logs")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Page<SystemAuditLogDTO>> logs(
            @RequestParam(required = false) LocalDateTime from,
            @RequestParam(required = false) LocalDateTime to,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(analyticsService.getSystemLogs(
                from == null ? LocalDateTime.now().minusDays(7) : from,
                to == null ? LocalDateTime.now().plusDays(1) : to,
                status,
                page,
                size));
    }
 
    @GetMapping("/api/analytics/spending/{userId}")
    public ResponseEntity<UserSpendingAnalyticsDTO> spending(@PathVariable Long userId,
                                                             @RequestParam(defaultValue = "6") int months) {
        currentUserService.assertOwner(userId);
        return ResponseEntity.ok(analyticsService.getUserSpendingAnalytics(userId, months));
    }
 
    @GetMapping("/api/analytics/wealth/{userId}")
    public ResponseEntity<UserWealthAnalyticsDTO> wealth(@PathVariable Long userId) {
        currentUserService.assertOwner(userId);
        return ResponseEntity.ok(analyticsService.getUserWealthAnalytics(userId));
    }
}
 