package com.neobank.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "loan_repayments")
public class LoanRepayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "loan_account_id")
    private LoanAccount loanAccount;

    private Integer instalmentNumber;
    private LocalDate dueDate;

    private Double emiAmount;
    private Double principalComponent;
    private Double interestComponent;

    @Column(name = "paid_at")
    private LocalDateTime paymentDate;

    @Enumerated(EnumType.STRING)
    private Status paymentStatus;

    public enum Status {
        PENDING, PAID, OVERDUE
    }

    @PrePersist
    public void init() {
        paymentStatus = Status.PENDING;
    }

    public Long getId() { return id; }

    public LoanAccount getLoanAccount() { return loanAccount; }

    public void setLoanAccount(LoanAccount loanAccount) { this.loanAccount = loanAccount; }

    public Integer getInstalmentNumber() { return instalmentNumber; }

    public void setInstalmentNumber(Integer instalmentNumber) { this.instalmentNumber = instalmentNumber; }

    public LocalDate getDueDate() { return dueDate; }

    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public Double getEmiAmount() { return emiAmount; }

    public void setEmiAmount(Double emiAmount) { this.emiAmount = emiAmount; }

    public Double getPrincipalComponent() { return principalComponent; }

    public void setPrincipalComponent(Double principalComponent) { this.principalComponent = principalComponent; }

    public Double getInterestComponent() { return interestComponent; }

    public void setInterestComponent(Double interestComponent) { this.interestComponent = interestComponent; }

    public Status getPaymentStatus() { return paymentStatus; }

    public void setPaymentStatus(Status paymentStatus) { this.paymentStatus = paymentStatus; }

    public LocalDateTime getPaymentDate() { return paymentDate; }

    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }
}