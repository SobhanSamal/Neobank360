package com.neobank.dto;
 
import com.neobank.entity.Budget;
import java.math.BigDecimal;
 
public class BudgetResponseDTO {
    private Long id;
    private Long userId;
    private String category;
    private String budgetMonth;
    private BigDecimal limitAmount;
 
    public static BudgetResponseDTO from(Budget budget) {
        BudgetResponseDTO dto = new BudgetResponseDTO();
        dto.setId(budget.getId());
        dto.setUserId(budget.getUser().getId());
        dto.setCategory(budget.getCategory().name());
        dto.setBudgetMonth(budget.getBudgetMonth().toString().substring(0, 7));
        dto.setLimitAmount(budget.getLimitAmount());
        return dto;
    }
 
    public Long getId() {
        return id;
    }
 
    public void setId(Long id) {
        this.id = id;
    }
 
    public Long getUserId() {
        return userId;
    }
 
    public void setUserId(Long userId) {
        this.userId = userId;
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
 
}
 