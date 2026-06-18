package com.neobank.dto;
 
import java.util.List;
 
public class UserActivityDTO {
 
    private Long userId;
    private List<TransactionResponse> transactions;
    private List<String> loginEvents;
 
    public UserActivityDTO() {
    }
 
    public UserActivityDTO(Long userId, List<TransactionResponse> transactions, List<String> loginEvents) {
        this.userId = userId;
        this.transactions = transactions;
        this.loginEvents = loginEvents;
    }
 
    public Long getUserId() {
        return userId;
    }
 
    public void setUserId(Long userId) {
        this.userId = userId;
    }
 
    public List<TransactionResponse> getTransactions() {
        return transactions;
    }
 
    public void setTransactions(List<TransactionResponse> transactions) {
        this.transactions = transactions;
    }
 
    public List<String> getLoginEvents() {
        return loginEvents;
    }
 
    public void setLoginEvents(List<String> loginEvents) {
        this.loginEvents = loginEvents;
    }
}
 