package com.neobank.dto;
 
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
 
public class BillRequestDTO {
 
    @NotNull(message = "accountId is required")
    private Long accountId;   // ✅ NEW
 
 
    public Long getAccountId() {
                return accountId;
        }
 
        public void setAccountId(Long accountId) {
                this.accountId = accountId;
        }
 
        @NotBlank(message = "billerName is required")
    private String billerName;
 
    @NotNull
    @Positive(message = "amount must be greater than zero")
    private BigDecimal amount;
 
    @NotNull
    @Future(message = "dueDate must be future")
    private LocalDate dueDate;
 
    // ✅ CATEGORY
    @NotBlank(message = "category is required")
    private String category;
 
    // ✅ GETTERS & SETTERS
 
    public String getBillerName() { return billerName; }
 
    public void setBillerName(String billerName) { this.billerName = billerName; }
 
    public BigDecimal getAmount() { return amount; }
 
    public void setAmount(BigDecimal amount) { this.amount = amount; }
 
    public LocalDate getDueDate() { return dueDate; }
 
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
 
    public String getCategory() { return category; }
 
    public void setCategory(String category) { this.category = category; }
}
 