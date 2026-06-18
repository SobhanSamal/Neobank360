package com.neobank.dto;

public class LoanDecisionDTO {

    private String decision;
    private String remarks;

    public LoanDecisionDTO() {}

    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
