package com.neobank.entity;
 
import jakarta.persistence.*;
 
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
 
@Entity
@Table(
    name = "accounts",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_accounts_user_type", columnNames = {"user_id", "account_type"})
    }
)
public class Account {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    // Ownership bound to User
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
 
    @Column(name = "account_number", nullable = false, unique = true, length = 20)
    private String accountNumber;
 
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;
 
    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false)
    private AccountType accountType;
 
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
 
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
 
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactions = new ArrayList<>();
 
    public enum AccountType {
        SAVING,
        CURRENT
    }
 
    public Account() {
    }
 
    public Account(Long id, User user, String accountNumber, BigDecimal balance, AccountType accountType, LocalDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.accountType = accountType;
        this.createdAt = createdAt;
    }
 
    public Long getId() {
        return id;
    }
 
    public void setId(Long id) {
        this.id = id;
    }
 
    public User getUser() {
        return user;
    }
 
    public void setUser(User user) {
        this.user = user;
    }
 
    public String getAccountNumber() {
        return accountNumber;
    }
 
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
 
    public BigDecimal getBalance() {
        return balance;
    }
 
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
 
    public AccountType getAccountType() {
        return accountType;
    }
 
    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }
 
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
 
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
 
    public Boolean getIsActive() {
        return isActive;
    }
 
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
 
    public List<Transaction> getTransactions() {
        return transactions;
    }
 
    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
}
 