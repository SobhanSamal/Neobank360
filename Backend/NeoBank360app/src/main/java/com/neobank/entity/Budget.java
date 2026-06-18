package com.neobank.entity;
 
import jakarta.persistence.*;
 
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
 
@Entity
@Table(
        name = "budgets",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_budget",
                        columnNames = {"user_id", "category", "budget_month"}
                )
        }
)
public class Budget {
 
    /* ========================
       Primary Key
       ======================== */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    /* ========================
       Relationships
       ======================== */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
 
    /* ========================
       Budget Fields
       ======================== */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BudgetCategory category;
 
    @Column(name = "budget_month", nullable = false)
    private LocalDate budgetMonth;
 
    @Column(name = "limit_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal limitAmount;
 
    @Column(name = "spent_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal spentAmount;
 
    @Column(name = "remaining_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal remainingAmount;
 
    /* ========================
       Audit Fields
       ======================== */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
 
    /* ========================
       Lifecycle Hook
       ======================== */
    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
 
        if (this.spentAmount == null) {
            this.spentAmount = BigDecimal.ZERO;
        }
 
        if (this.remainingAmount == null && this.limitAmount != null) {
            this.remainingAmount = this.limitAmount.subtract(this.spentAmount);
        }
    }
 
    /* ========================
       Getters & Setters
       ======================== */
    public Long getId() {
        return id;
    }
 
    public User getUser() {
        return user;
    }
 
    public void setUser(User user) {
        this.user = user;
    }
 
    public BudgetCategory getCategory() {
        return category;
    }
 
    public void setCategory(BudgetCategory category) {
        this.category = category;
    }
 
    public LocalDate getBudgetMonth() {
        return budgetMonth;
    }
 
    public void setBudgetMonth(LocalDate budgetMonth) {
        this.budgetMonth = budgetMonth;
    }
 
    public BigDecimal getLimitAmount() {
        return limitAmount;
    }
 
    public void setLimitAmount(BigDecimal limitAmount) {
        this.limitAmount = limitAmount;
    }
 
    public BigDecimal getSpentAmount() {
        return spentAmount;
    }
 
    public void setSpentAmount(BigDecimal spentAmount) {
        this.spentAmount = spentAmount;
    }
 
    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }
 
    public void setRemainingAmount(BigDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
    }
 
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
 