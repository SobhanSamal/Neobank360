package com.neobank.dto;

import com.neobank.entity.Account;

import java.math.BigDecimal;

public class AccountResponse {

    private Long id;
    private String accountNumber;
    private String accountType;
    private BigDecimal balance;

    public AccountResponse() {
    }

    public AccountResponse(Long id, String accountNumber, String accountType, BigDecimal balance) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = balance;
    }

    public static AccountResponse from(Account account) {
        return new AccountResponse(
            account.getId(),
            account.getAccountNumber(),
            account.getAccountType().name(),
            account.getBalance()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}