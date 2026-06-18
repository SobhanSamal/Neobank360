package com.neobank.dto;

public class LoanProductDTO {

    public Long id;

    public String productName;

    public Double minAmount;
    public Double maxAmount;

    public Double annualInterestRate;

    public String allowedTenures;

    public LoanProductDTO() {}

    public LoanProductDTO(Long id, String productName,
                          Double minAmount, Double maxAmount,
                          Double annualInterestRate,
                          String allowedTenures) {

        this.id = id;
        this.productName = productName;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.annualInterestRate = annualInterestRate;
        this.allowedTenures = allowedTenures;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Double getMinAmount() {
		return minAmount;
	}

	public void setMinAmount(Double minAmount) {
		this.minAmount = minAmount;
	}

	public Double getMaxAmount() {
		return maxAmount;
	}

	public void setMaxAmount(Double maxAmount) {
		this.maxAmount = maxAmount;
	}

	public Double getAnnualInterestRate() {
		return annualInterestRate;
	}

	public void setAnnualInterestRate(Double annualInterestRate) {
		this.annualInterestRate = annualInterestRate;
	}

	public String getAllowedTenures() {
		return allowedTenures;
	}

	public void setAllowedTenures(String allowedTenures) {
		this.allowedTenures = allowedTenures;
	}

	@Override
	public String toString() {
		return "LoanProductDTO [id=" + id + ", productName=" + productName + ", minAmount=" + minAmount + ", maxAmount="
				+ maxAmount + ", annualInterestRate=" + annualInterestRate + ", allowedTenures=" + allowedTenures + "]";
	}
    
  
}
