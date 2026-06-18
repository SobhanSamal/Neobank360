package com.neobank.dto;
 
import com.neobank.entity.Bill;
 
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
 
public class BillResponseDTO {
 
    private Long id;
    private Long userId;
 
    private Long accountId;   // ✅ NEW
    private String accountNumber;
    private String accountType;
 
    private String billerName;
    private BigDecimal amount;
    private LocalDate dueDate;
    private String status;
    private boolean remindMe;
    private String category; // ✅ NEW
    private LocalDateTime paidAt;
 
    public static BillResponseDTO from(Bill bill, boolean remindMe) {
 
        BillResponseDTO dto = new BillResponseDTO();
 
        dto.setId(bill.getId());
        dto.setUserId(bill.getUser().getId());
        dto.setAccountId(bill.getAccount().getId()); // ✅
        dto.setAccountNumber(bill.getAccount().getAccountNumber());
        dto.setAccountType(
                bill.getAccount().getAccountType() != null
                        ? bill.getAccount().getAccountType().name()
                        : null
        );
 
        dto.setBillerName(bill.getBillerName());
        dto.setAmount(bill.getAmount());
        dto.setDueDate(bill.getDueDate());
        dto.setStatus(bill.getStatus().name());
        dto.setRemindMe(remindMe);
        dto.setCategory(bill.getCategory()); // ✅
        dto.setPaidAt(bill.getPaidAt());
 
        return dto;
    }
 
    // ✅ GETTERS & SETTERS
 
    public Long getAccountId() {
                return accountId;
        }
 
        public void setAccountId(Long accountId) {
                this.accountId = accountId;
        }
 
    public String getAccountNumber() {
                return accountNumber;
        }
 
        public void setAccountNumber(String accountNumber) {
                this.accountNumber = accountNumber;
        }
 
    public String getAccountType() {
                return accountType;
        }
 
        public void setAccountType(String accountType) {
                this.accountType = accountType;
        }
 
        public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
 
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
 
    public String getBillerName() { return billerName; }
    public void setBillerName(String billerName) { this.billerName = billerName; }
 
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
 
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
 
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
 
    public boolean isRemindMe() { return remindMe; }
    public void setRemindMe(boolean remindMe) { this.remindMe = remindMe; }
 
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
 
    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }
}
 