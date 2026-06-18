package com.neobank.dto;
 
import java.math.BigDecimal;
import java.time.LocalDate;
 
public class DailyVolumeDTO {
    private LocalDate date;
    private BigDecimal totalAmount;
    private Long transactionCount;
 
    public DailyVolumeDTO() {}
 
    public DailyVolumeDTO(LocalDate date, BigDecimal totalAmount, Long transactionCount) {
        this.date = date;
        this.totalAmount = totalAmount;
        this.transactionCount = transactionCount;
    }
 
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public Long getTransactionCount() { return transactionCount; }
    public void setTransactionCount(Long transactionCount) { this.transactionCount = transactionCount; }
}
 