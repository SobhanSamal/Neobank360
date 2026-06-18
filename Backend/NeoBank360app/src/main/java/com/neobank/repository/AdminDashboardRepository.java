package com.neobank.repository;

import com.neobank.entity.User;
import com.neobank.entity.Account;
import com.neobank.entity.Transaction;
import com.neobank.entity.LoanApplication;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

public interface AdminDashboardRepository extends Repository<User, Long> {

    /* ✅ TOTAL USERS */
    @Query("SELECT COUNT(u) FROM User u")
    long totalUsers();

    /* ✅ ACTIVE USERS */
    @Query("SELECT COUNT(u) FROM User u WHERE u.isActive = true")
    long totalActiveUsers();

    /* ✅ TOTAL ACCOUNTS (WORKING ✅ because table shows 14) */
    @Query("SELECT COUNT(a) FROM Account a")
    long totalAccounts();

    /* ✅ TOTAL LOANS */
    @Query("SELECT COUNT(l) FROM LoanApplication l")
    long totalLoans();

    /* ✅ FIXED ENUM ISSUE ✅ */
    @Query("""
        SELECT COUNT(l)
        FROM LoanApplication l
        WHERE l.status = 'PENDING'
    """)
    long pendingApprovals();

    /* ✅ TRANSACTION COUNT */
    @Query("SELECT COUNT(t) FROM Transaction t")
    long totalTransactions();
    
    
}