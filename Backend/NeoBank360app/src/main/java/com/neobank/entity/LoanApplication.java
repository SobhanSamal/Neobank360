package com.neobank.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "loan_applications")
public class LoanApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* ✅ RELATIONS */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_product_id", nullable = false)
    private LoanProduct loanProduct;

    /* ✅ REQUEST DATA */
    @Column(nullable = false)
    private Double requestedAmount;

    @Column(nullable = false)
    private Integer requestedTenureMonths;

    /* ✅ STATUS */
    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        PENDING, APPROVED, REJECTED
    }

    private String adminRemarks;

    private LocalDateTime appliedAt;
    private LocalDateTime decidedAt;

    @PrePersist
    public void onCreate() {
        this.status = Status.PENDING;
        this.appliedAt = LocalDateTime.now();
    }

    /* ✅ GETTERS/SETTERS */
    public Long getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public LoanProduct getLoanProduct() { return loanProduct; }
    public void setLoanProduct(LoanProduct loanProduct) { this.loanProduct = loanProduct; }

    public Double getRequestedAmount() { return requestedAmount; }
    public void setRequestedAmount(Double requestedAmount) { this.requestedAmount = requestedAmount; }

    public Integer getRequestedTenureMonths() { return requestedTenureMonths; }
    public void setRequestedTenureMonths(Integer requestedTenureMonths) { this.requestedTenureMonths = requestedTenureMonths; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getAdminRemarks() { return adminRemarks; }
    public void setAdminRemarks(String adminRemarks) { this.adminRemarks = adminRemarks; }

    public LocalDateTime getAppliedAt() { return appliedAt; }
    public LocalDateTime getDecidedAt() { return decidedAt; }
    public void setDecidedAt(LocalDateTime decidedAt) { this.decidedAt = decidedAt; }
}
