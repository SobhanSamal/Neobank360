package com.neobank.dto;
 
import java.math.BigDecimal;
 
public class BudgetSummaryDTO {
 
    private String category;
    private String budgetMonth;
    private BigDecimal limitAmount;
    private BigDecimal spentAmount;
    private BigDecimal remainingAmount;
    private BigDecimal utilizationPercentage;
 
    public BudgetSummaryDTO(
            String category,
            String budgetMonth,
            BigDecimal limitAmount,
            BigDecimal spentAmount,
            BigDecimal remainingAmount,
            BigDecimal utilizationPercentage
    ) {
        this.category = category;
        this.budgetMonth = budgetMonth;
        this.limitAmount = limitAmount;
        this.spentAmount = spentAmount;
        this.remainingAmount = remainingAmount;
        this.utilizationPercentage = utilizationPercentage;
    }
 
    public String getCategory() {
        return category;
    }
 
    public void setCategory(String category) {
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
 
    public BigDecimal getSpentAmount() {
        return spentAmount;
    }
 
    public void setSpentAmount(BigDecimal spentAmount) {
        this.spentAmount = spentAmount;
    }
 
    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }
 
    public void setRemainingAmount(BigDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
    }
 
    public BigDecimal getUtilizationPercentage() {
        return utilizationPercentage;
    }
 
    public void setUtilizationPercentage(BigDecimal utilizationPercentage) {
        this.utilizationPercentage = utilizationPercentage;
    }
}
 