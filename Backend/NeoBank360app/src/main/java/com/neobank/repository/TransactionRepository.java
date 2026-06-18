package com.neobank.repository;
 
import com.neobank.entity.BudgetCategory;
import com.neobank.entity.Transaction;
import com.neobank.entity.User;
 
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
 
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
 
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
 
    /* =========================
       BASIC QUERIES
    ========================= */
 
    List<Transaction> findByAccountIdOrderByTransactionDateDesc(
            Long accountId
    );
 
    Page<Transaction> findByAccountIdOrderByTransactionDateDesc(
            Long accountId,
            Pageable pageable
    );
 
    List<Transaction> findByAccountId(
            Long accountId
    );
    List<Transaction> findByTransactionDateBetween(LocalDateTime start, LocalDateTime end);
    /* =========================
       USER ACTIVITY
    ========================= */
 
    List<Transaction> findTop20ByAccountUserIdOrderByTransactionDateDesc(Long userId);
 
    /* =========================
       FILTERED TRANSACTIONS
    ========================= */
 
    List<Transaction> findByAccountUserIdAndTypeAndTransactionDateBetween(
            Long userId,
            Transaction.TransactionType type,
            LocalDateTime start,
            LocalDateTime end
    );
 
    /* =========================
       ✅ FIXED BUDGET QUERY (IMPORTANT 🔥)
    ========================= */
 
    @Query("""
            SELECT COALESCE(SUM(t.amount), 0)
            FROM Transaction t
            WHERE t.account.user = :user
              AND t.type = 'DEBIT'
              AND LOWER(TRIM(t.description)) = LOWER(TRIM(:categoryName))
              AND t.transactionDate >= :startDate
              AND t.transactionDate < :endDate
        """)
        BigDecimal getTotalSpentByCategoryAndMonth(
                @Param("user") User user,
                @Param("categoryName") String categoryName,
                @Param("startDate") LocalDateTime startDate,
                @Param("endDate") LocalDateTime endDate
        );
 
}
 