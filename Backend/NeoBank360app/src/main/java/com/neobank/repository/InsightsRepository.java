package com.neobank.repository;

import com.neobank.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface InsightsRepository extends JpaRepository<Transaction, Long> {

    /* ✅ TOTAL INCOME */
    @Query("""
        SELECT COALESCE(SUM(t.amount), 0)
        FROM Transaction t
        WHERE t.account.user.id = :userId
          AND t.type = 'CREDIT'
    """)
    BigDecimal getTotalIncome(@Param("userId") Long userId);

    /* ✅ TOTAL EXPENSE */
    @Query("""
        SELECT COALESCE(SUM(t.amount), 0)
        FROM Transaction t
        WHERE t.account.user.id = :userId
          AND t.type = 'DEBIT'
    """)
    BigDecimal getTotalExpense(@Param("userId") Long userId);

    /* ✅ TREND (FIXED ✅) */
    @Query("""
        SELECT YEAR(t.transactionDate), MONTH(t.transactionDate),
               COALESCE(SUM(CASE WHEN t.type = 'CREDIT' THEN t.amount ELSE 0 END),0),
               COALESCE(SUM(CASE WHEN t.type = 'DEBIT' THEN t.amount ELSE 0 END),0)
        FROM Transaction t
        WHERE t.account.user.id = :userId
          AND t.transactionDate >= :startDate
        GROUP BY YEAR(t.transactionDate), MONTH(t.transactionDate)
    """)
    List<Object[]> getMonthlyTrend(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate);
}
