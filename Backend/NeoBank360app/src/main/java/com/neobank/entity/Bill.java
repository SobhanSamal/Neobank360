package com.neobank.entity;
 
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
 
@Entity
@Table(name = "bills")
public class Bill {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    

@ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id")
    private Account account;

 
    public Account getAccount() {
	return account;
}

public void setAccount(Account account) {
	this.account = account;
}

	@Column(name = "biller_name", nullable = false)
    private String billerName;
 
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;
 
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;
 
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BillStatus status = BillStatus.PENDING;
 
    // ✅ NEW CATEGORY FIELD
    @Column(name = "category")
    private String category;
 
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
 
    @Column(name = "paid_at")
    private LocalDateTime paidAt;
 
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = BillStatus.PENDING;
        }
    }
 
    public enum BillStatus {
        PENDING, PAID, OVERDUE
    }
 
    // ✅ GETTERS & SETTERS
 
    public Long getId() { return id; }
 
    public void setId(Long id) { this.id = id; }
 
    public User getUser() { return user; }
 
    public void setUser(User user) { this.user = user; }
 
    public String getBillerName() { return billerName; }
 
    public void setBillerName(String billerName) { this.billerName = billerName; }
 
    public BigDecimal getAmount() { return amount; }
 
    public void setAmount(BigDecimal amount) { this.amount = amount; }
 
    public LocalDate getDueDate() { return dueDate; }
 
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
 
    public BillStatus getStatus() { return status; }
 
    public void setStatus(BillStatus status) { this.status = status; }
 
    public String getCategory() { return category; }
 
    public void setCategory(String category) { this.category = category; }
 
    public LocalDateTime getCreatedAt() { return createdAt; }
 
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
 
    public LocalDateTime getPaidAt() { return paidAt; }
 
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }
}
 