package com.neobank.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "loan_accounts")
public class LoanAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "loan_application_id", unique = true)
    private LoanApplication loanApplication;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Double principalAmount;
    private Double annualInterestRate;
    private Integer tenureMonths;
    private Double emiAmount;

    private LocalDateTime disbursedAt;

    @PrePersist
    public void prePersist() {
        disbursedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public LoanApplication getLoanApplication() { return loanApplication; }
    public void setLoanApplication(LoanApplication loanApplication) { this.loanApplication = loanApplication; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Double getPrincipalAmount() { return principalAmount; }
    public void setPrincipalAmount(Double principalAmount) { this.principalAmount = principalAmount; }
    public Double getAnnualInterestRate() { return annualInterestRate; }
    public void setAnnualInterestRate(Double annualInterestRate) { this.annualInterestRate = annualInterestRate; }
    public Integer getTenureMonths() { return tenureMonths; }
    public void setTenureMonths(Integer tenureMonths) { this.tenureMonths = tenureMonths; }
    public Double getEmiAmount() { return emiAmount; }
    public void setEmiAmount(Double emiAmount) { this.emiAmount = emiAmount; }
    public LocalDateTime getDisbursedAt() { return disbursedAt; }
}