package com.neobank.dto;
 
import com.neobank.entity.BudgetCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
 
import java.math.BigDecimal;
 
public class BudgetRequestDTO {
 
    @NotNull(message = "category is required")
    private BudgetCategory category;
 
    @NotBlank(message = "budgetMonth is required")
    private String budgetMonth;
 
    @NotNull(message = "limitAmount is required")
    @Positive(message = "limitAmount must be greater than zero")
    private BigDecimal limitAmount;
 
    public BudgetCategory getCategory() {
        return category;
    }
 
    public void setCategory(BudgetCategory category) {
        this.category = category;
    }
 
    public String getBudgetMonth() {
        return budgetMonth;
    }
 
    public void setBudgetMonth(String budgetMonth) {
        this.budgetMonth = budgetMonth;
    }
 
    public BigDecimal getLimitAmount() {
        return limitAmount;
    }
 
    public void setLimitAmount(BigDecimal limitAmount) {
        this.limitAmount = limitAmount;
    }
}
 