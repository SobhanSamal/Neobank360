package com.neobank.dto;

import java.math.BigDecimal;

public class TrendEntryDTO {

    private String month;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;

    public TrendEntryDTO() {}

    public TrendEntryDTO(String month,
                         BigDecimal totalIncome,
                         BigDecimal totalExpense) {
        this.month = month;
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
    }

    public String getMonth() { return month; }
    public BigDecimal getTotalIncome() { return totalIncome; }
    public BigDecimal getTotalExpense() { return totalExpense; }

    public void setMonth(String month) { this.month = month; }
    public void setTotalIncome(BigDecimal totalIncome) { this.totalIncome = totalIncome; }
    public void setTotalExpense(BigDecimal totalExpense) { this.totalExpense = totalExpense; }
}
