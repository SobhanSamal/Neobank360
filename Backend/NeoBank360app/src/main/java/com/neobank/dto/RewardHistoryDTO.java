package com.neobank.dto;
 
import com.neobank.entity.Bill;
 
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
 
public class RewardHistoryDTO {
    private Long billId;
    private String billerName;
    private String category;
    private BigDecimal billAmount;
    private BigDecimal rewardPoints;
    private LocalDate dueDate;
    private LocalDateTime paidAt;
 
    public static RewardHistoryDTO from(Bill bill) {
        RewardHistoryDTO dto = new RewardHistoryDTO();
        dto.setBillId(bill.getId());
        dto.setBillerName(bill.getBillerName());
        dto.setCategory(bill.getCategory());
        dto.setBillAmount(bill.getAmount());
        dto.setRewardPoints(bill.getAmount().divide(BigDecimal.valueOf(100)));
        dto.setDueDate(bill.getDueDate());
        dto.setPaidAt(bill.getPaidAt());
        return dto;
    }
 
    public Long getBillId() { return billId; }
    public void setBillId(Long billId) { this.billId = billId; }
 
    public String getBillerName() { return billerName; }
    public void setBillerName(String billerName) { this.billerName = billerName; }
 
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
 
    public BigDecimal getBillAmount() { return billAmount; }
    public void setBillAmount(BigDecimal billAmount) { this.billAmount = billAmount; }
 
    public BigDecimal getRewardPoints() { return rewardPoints; }
    public void setRewardPoints(BigDecimal rewardPoints) { this.rewardPoints = rewardPoints; }
 
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
 
    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }
}
 