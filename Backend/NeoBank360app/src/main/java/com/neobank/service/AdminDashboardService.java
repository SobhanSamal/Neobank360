package com.neobank.service;

import com.neobank.dto.AdminDashboardDTO;
import com.neobank.dto.PendingApprovalDTO;
import com.neobank.dto.SystemHealthDTO;
import com.neobank.repository.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.lang.management.ManagementFactory;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminDashboardService {

    private final AdminDashboardRepository repo;
    private final PendingApprovalRepository pendingRepo;
    private final DataSource dataSource;

    private final UserRepository userRepo;
    private final AccountRepository accountRepo;
    private final TransactionRepository transactionRepo;
    private final LoginEventRepository loginEventRepo;

    public AdminDashboardService(
            AdminDashboardRepository repo,
            PendingApprovalRepository pendingRepo,
            DataSource dataSource,
            UserRepository userRepo,
            AccountRepository accountRepo,
            TransactionRepository transactionRepo,
            LoginEventRepository loginEventRepo
    ) {
        this.repo = repo;
        this.pendingRepo = pendingRepo;
        this.dataSource = dataSource;
        this.userRepo = userRepo;
        this.accountRepo = accountRepo;
        this.transactionRepo = transactionRepo;
        this.loginEventRepo = loginEventRepo;
    }

    /* =========================
       DASHBOARD
    ========================= */
    @Transactional(readOnly = true)
    public AdminDashboardDTO buildDashboard() {

        long totalUsers = repo.totalUsers();
        long activeUsers = repo.totalActiveUsers();
        long loans = repo.totalLoans();
        long totalAccounts = repo.totalAccounts();
        long pending = repo.pendingApprovals();
        long transactions = repo.totalTransactions();

        return new AdminDashboardDTO(
                totalUsers,
                activeUsers,
                loans,
                totalAccounts,
                pending,
                transactions,
                0
        );
    }

    /* =========================
       PENDING APPROVALS
    ========================= */
    public List<PendingApprovalDTO> getPendingApprovals() {
        return pendingRepo.findPendingApprovals();
    }

    /* =========================
       SYSTEM HEALTH ✅
    ========================= */
    public SystemHealthDTO getSystemHealth() {

        String dbStatus = "DOWN";

        try (Connection conn = dataSource.getConnection()) {
            conn.prepareStatement("SELECT 1").execute();
            dbStatus = "UP";
        } catch (Exception ignored) {
            dbStatus = "DOWN";
        }

        long totalUsers = userRepo.count();
        long activeUsers = userRepo.countByIsActiveTrue();
        long totalAccounts = accountRepo.count();
        long totalTransactions = transactionRepo.count();

        long activeSessions = loginEventRepo.countActiveSessions(
                LocalDateTime.now().minusMinutes(30)
        );

        long uptime = ManagementFactory
                .getRuntimeMXBean()
                .getUptime() / 1000;

        return new SystemHealthDTO(
                dbStatus,
                activeUsers,
                totalUsers,
                totalAccounts,
                totalTransactions,
                uptime,
                activeSessions
        );
    }
    
}