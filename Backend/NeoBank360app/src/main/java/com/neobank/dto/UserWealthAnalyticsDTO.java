package com.neobank.dto;
 
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
 
public class UserWealthAnalyticsDTO {
    private List<NetWorthPointDTO> netWorthTimeline;
    private List<LoanPayoffForecastDTO> loanPayoffForecast;
    private List<RewardAccrualDTO> rewardAccrualHistory;
 
    public List<NetWorthPointDTO> getNetWorthTimeline() { return netWorthTimeline; }
    public void setNetWorthTimeline(List<NetWorthPointDTO> netWorthTimeline) { this.netWorthTimeline = netWorthTimeline; }
    public List<LoanPayoffForecastDTO> getLoanPayoffForecast() { return loanPayoffForecast; }
    public void setLoanPayoffForecast(List<LoanPayoffForecastDTO> loanPayoffForecast) { this.loanPayoffForecast = loanPayoffForecast; }
    public List<RewardAccrualDTO> getRewardAccrualHistory() { return rewardAccrualHistory; }
    public void setRewardAccrualHistory(List<RewardAccrualDTO> rewardAccrualHistory) { this.rewardAccrualHistory = rewardAccrualHistory; }
 
    public static class NetWorthPointDTO {
        private LocalDate month;
        private BigDecimal totalBalance;
        private BigDecimal outstandingPrincipal;
        private BigDecimal netWorth;
        public NetWorthPointDTO() {}
        public NetWorthPointDTO(LocalDate month, BigDecimal totalBalance, BigDecimal outstandingPrincipal, BigDecimal netWorth) {
            this.month = month; this.totalBalance = totalBalance; this.outstandingPrincipal = outstandingPrincipal; this.netWorth = netWorth;
        }
        public LocalDate getMonth() { return month; }
        public void setMonth(LocalDate month) { this.month = month; }
        public BigDecimal getTotalBalance() { return totalBalance; }
        public void setTotalBalance(BigDecimal totalBalance) { this.totalBalance = totalBalance; }
        public BigDecimal getOutstandingPrincipal() { return outstandingPrincipal; }
        public void setOutstandingPrincipal(BigDecimal outstandingPrincipal) { this.outstandingPrincipal = outstandingPrincipal; }
        public BigDecimal getNetWorth() { return netWorth; }
        public void setNetWorth(BigDecimal netWorth) { this.netWorth = netWorth; }
    }
 
    public static class LoanPayoffForecastDTO {
        private Long loanAccountId;
        private Integer monthsRemaining;
        private LocalDate projectedPayoffDate;
        public LoanPayoffForecastDTO() {}
        public LoanPayoffForecastDTO(Long loanAccountId, Integer monthsRemaining, LocalDate projectedPayoffDate) {
            this.loanAccountId = loanAccountId; this.monthsRemaining = monthsRemaining; this.projectedPayoffDate = projectedPayoffDate;
        }
        public Long getLoanAccountId() { return loanAccountId; }
        public void setLoanAccountId(Long loanAccountId) { this.loanAccountId = loanAccountId; }
        public Integer getMonthsRemaining() { return monthsRemaining; }
        public void setMonthsRemaining(Integer monthsRemaining) { this.monthsRemaining = monthsRemaining; }
        public LocalDate getProjectedPayoffDate() { return projectedPayoffDate; }
        public void setProjectedPayoffDate(LocalDate projectedPayoffDate) { this.projectedPayoffDate = projectedPayoffDate; }
    }
 
    public static class RewardAccrualDTO {
        private LocalDate month;
        private BigDecimal rewardPoints;
        public RewardAccrualDTO() {}
        public RewardAccrualDTO(LocalDate month, BigDecimal rewardPoints) { this.month = month; this.rewardPoints = rewardPoints; }
        public LocalDate getMonth() { return month; }
        public void setMonth(LocalDate month) { this.month = month; }
        public BigDecimal getRewardPoints() { return rewardPoints; }
        public void setRewardPoints(BigDecimal rewardPoints) { this.rewardPoints = rewardPoints; }
    }
}
 