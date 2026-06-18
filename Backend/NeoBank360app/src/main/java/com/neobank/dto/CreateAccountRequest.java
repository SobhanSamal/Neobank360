package com.neobank.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateAccountRequest {

    @NotBlank(message = "accountType is required")
    private String accountType; // SAVINGS or CURRENT

    public CreateAccountRequest() {
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
}