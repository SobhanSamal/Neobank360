package com.neobank.dto;

import java.math.BigDecimal;
import java.util.List;

public class FinancialInsightsDTO {

    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal savings;
    private List<TrendEntryDTO> trendSummary;

    public FinancialInsightsDTO() {}

    public FinancialInsightsDTO(
            BigDecimal totalIncome,
            BigDecimal totalExpense,
            BigDecimal savings,
            List<TrendEntryDTO> trendSummary) {

        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.savings = savings;
        this.trendSummary = trendSummary;
    }

    public BigDecimal getTotalIncome() { return totalIncome; }
    public BigDecimal getTotalExpense() { return totalExpense; }
    public BigDecimal getSavings() { return savings; }
    public List<TrendEntryDTO> getTrendSummary() { return trendSummary; }

    public void setTotalIncome(BigDecimal totalIncome) { this.totalIncome = totalIncome; }
    public void setTotalExpense(BigDecimal totalExpense) { this.totalExpense = totalExpense; }
    public void setSavings(BigDecimal savings) { this.savings = savings; }
    public void setTrendSummary(List<TrendEntryDTO> trendSummary) { this.trendSummary = trendSummary; }
}
