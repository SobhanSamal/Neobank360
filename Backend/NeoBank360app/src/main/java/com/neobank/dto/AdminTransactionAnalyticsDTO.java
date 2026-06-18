package com.neobank.dto;
 
import java.math.BigDecimal;
import java.util.List;
 
public class AdminTransactionAnalyticsDTO {
    private List<DailyVolumeDTO> dailyVolumes;
    private BigDecimal averageTicketSize;
    private BigDecimal totalInflow;
    private BigDecimal totalOutflow;
 
    public List<DailyVolumeDTO> getDailyVolumes() { return dailyVolumes; }
    public void setDailyVolumes(List<DailyVolumeDTO> dailyVolumes) { this.dailyVolumes = dailyVolumes; }
    public BigDecimal getAverageTicketSize() { return averageTicketSize; }
    public void setAverageTicketSize(BigDecimal averageTicketSize) { this.averageTicketSize = averageTicketSize; }
    public BigDecimal getTotalInflow() { return totalInflow; }
    public void setTotalInflow(BigDecimal totalInflow) { this.totalInflow = totalInflow; }
    public BigDecimal getTotalOutflow() { return totalOutflow; }
    public void setTotalOutflow(BigDecimal totalOutflow) { this.totalOutflow = totalOutflow; }
}
 