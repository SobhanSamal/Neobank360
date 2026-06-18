package com.neobank.dto;

public class LoanApplicationResponseDTO {

    public Long id;
    public String status;
    public String message;

    // ✅ DAY‑24 fields
    public String userEmail;
    public String productName;
    public Double requestedAmount;
    public Integer requestedTenureMonths;

    public LoanApplicationResponseDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
