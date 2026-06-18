package com.neobank.dto;
 
import java.math.BigDecimal;
import java.util.Map;
 
public class AdminLoanAnalyticsDTO {
    private Map<String, Long> loanDistribution;
    private Long npaCount;
    private BigDecimal npaRatio;
 
    public Map<String, Long> getLoanDistribution() { return loanDistribution; }
    public void setLoanDistribution(Map<String, Long> loanDistribution) { this.loanDistribution = loanDistribution; }
    public Long getNpaCount() { return npaCount; }
    public void setNpaCount(Long npaCount) { this.npaCount = npaCount; }
    public BigDecimal getNpaRatio() { return npaRatio; }
    public void setNpaRatio(BigDecimal npaRatio) { this.npaRatio = npaRatio; }
}
 