package com.neobank.dto;

import java.time.LocalDateTime;

public class PendingApprovalDTO {

    private Long id;
    private String type;
    private String applicantName;
    private String productName;
    private Double requestedAmount;
    private LocalDateTime appliedAt;

    public PendingApprovalDTO(Long id, String type,
                              String applicantName,
                              String productName,
                              Double requestedAmount,
                              LocalDateTime appliedAt) {

        this.id = id;
        this.type = type;
        this.applicantName = applicantName;
        this.productName = productName;
        this.requestedAmount = requestedAmount;
        this.appliedAt = appliedAt;
    }

    public Long getId() { return id; }
    public String getType() { return type; }
    public String getApplicantName() { return applicantName; }
    public String getProductName() { return productName; }
    public Double getRequestedAmount() { return requestedAmount; }
    public LocalDateTime getAppliedAt() { return appliedAt; }
}
