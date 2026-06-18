package com.neobank.dto;
 
import java.math.BigDecimal;
import java.util.List;
 
public class UserSpendingAnalyticsDTO {
    private List<CategorySpendDTO> categorySpending;
    private List<BudgetVsSpendDTO> budgetVsActual;
 
    public List<CategorySpendDTO> getCategorySpending() { return categorySpending; }
    public void setCategorySpending(List<CategorySpendDTO> categorySpending) { this.categorySpending = categorySpending; }
    public List<BudgetVsSpendDTO> getBudgetVsActual() { return budgetVsActual; }
    public void setBudgetVsActual(List<BudgetVsSpendDTO> budgetVsActual) { this.budgetVsActual = budgetVsActual; }
 
    public static class CategorySpendDTO {
        private String category;
        private BigDecimal amount;
        public CategorySpendDTO() {}
        public CategorySpendDTO(String category, BigDecimal amount) { this.category = category; this.amount = amount; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
    }
 
    public static class BudgetVsSpendDTO {
        private String category;
        private BigDecimal budgetLimit;
        private BigDecimal actualSpend;
        public BudgetVsSpendDTO() {}
        public BudgetVsSpendDTO(String category, BigDecimal budgetLimit, BigDecimal actualSpend) {
            this.category = category; this.budgetLimit = budgetLimit; this.actualSpend = actualSpend;
        }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public BigDecimal getBudgetLimit() { return budgetLimit; }
        public void setBudgetLimit(BigDecimal budgetLimit) { this.budgetLimit = budgetLimit; }
        public BigDecimal getActualSpend() { return actualSpend; }
        public void setActualSpend(BigDecimal actualSpend) { this.actualSpend = actualSpend; }
    }
}
 