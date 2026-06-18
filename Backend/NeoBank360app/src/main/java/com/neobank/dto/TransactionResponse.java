package com.neobank.dto;

import com.neobank.entity.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionResponse {

    private Long id;
    private Long accountId;
    private String accountNumber;
    private String type;
    private BigDecimal amount;
    private String description;
    private BigDecimal balanceAfter;
    private LocalDateTime transactionDate;

    public TransactionResponse() {
    }

    public TransactionResponse(Long id, Long accountId, String accountNumber, String type,
                                BigDecimal amount, String description,
                                BigDecimal balanceAfter, LocalDateTime transactionDate) {
        this.id = id;
        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.balanceAfter = balanceAfter;
        this.transactionDate = transactionDate;
    }

    public static TransactionResponse from(Transaction t) {
        return new TransactionResponse(
            t.getId(),
            t.getAccount().getId(),
            t.getAccount().getAccountNumber(),
            t.getType().name(),
            t.getAmount(),
            t.getDescription(),
            t.getBalanceAfter(),
            t.getTransactionDate()
        );
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getBalanceAfter() { return balanceAfter; }
    public void setBalanceAfter(BigDecimal balanceAfter) { this.balanceAfter = balanceAfter; }

    public LocalDateTime getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }
}
