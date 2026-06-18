package com.neobank.dto;

import java.time.LocalDate;

public class RepaymentScheduleDTO {

    public Long id;                  // ✅ ADD THIS (IMPORTANT FIX)

    public int instalmentNumber;

    public LocalDate dueDate;          // ✅ NEW

    public Double emiAmount;
    public Double principalComponent;
    public Double interestComponent;

    public String paymentStatus;       // ✅ NEW

	public int getInstalmentNumber() {
		return instalmentNumber;
	}

	public void setInstalmentNumber(int instalmentNumber) {
		this.instalmentNumber = instalmentNumber;
	}

	public LocalDate getDueDate() {
		return dueDate;
	}

	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}

	public Double getEmiAmount() {
		return emiAmount;
	}

	public void setEmiAmount(Double emiAmount) {
		this.emiAmount = emiAmount;
	}

	public Double getPrincipalComponent() {
		return principalComponent;
	}

	public void setPrincipalComponent(Double principalComponent) {
		this.principalComponent = principalComponent;
	}

	public Double getInterestComponent() {
		return interestComponent;
	}

	public void setInterestComponent(Double interestComponent) {
		this.interestComponent = interestComponent;
	}

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}
    
    
}
