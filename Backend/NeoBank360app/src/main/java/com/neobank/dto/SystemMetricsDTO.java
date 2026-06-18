package com.neobank.dto;

public class SystemMetricsDTO {

    // ✅ Date (day or hour bucket)
    private String date;

    // ✅ Total number of API requests
    private long totalRequests;

    // ✅ Number of failed requests (4xx + 5xx)
    private long errorCount;

    // ✅ Error percentage
    private double errorRate;

    // ✅ Average response time (ms)
    private double avgResponseTime;

    // ✅ Getters & Setters

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getTotalRequests() {
        return totalRequests;
    }

    public void setTotalRequests(long totalRequests) {
        this.totalRequests = totalRequests;
    }

    public long getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(long errorCount) {
        this.errorCount = errorCount;
    }

    public double getErrorRate() {
        return errorRate;
    }

    public void setErrorRate(double errorRate) {
        this.errorRate = errorRate;
    }

    public double getAvgResponseTime() {
        return avgResponseTime;
    }

    public void setAvgResponseTime(double avgResponseTime) {
        this.avgResponseTime = avgResponseTime;
    }
}