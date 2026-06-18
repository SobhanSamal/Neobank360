package com.neobank.dto;

public class AdminDashboardDTO {

    private long totalUsers;
    private long totalActiveUsers;
    private long totalLoans;
    private long totalAccounts;
    private long pendingApprovals;
    private long totalTransactions;
    private double platformSavingsRate;

    public AdminDashboardDTO(long totalUsers, long totalActiveUsers,
                             long totalLoans, long totalAccounts, long pendingApprovals,
                             long totalTransactions, double platformSavingsRate) {
        this.totalUsers = totalUsers;
        this.totalActiveUsers = totalActiveUsers;
        this.totalLoans = totalLoans;
        this.totalAccounts = totalAccounts;
        this.pendingApprovals = pendingApprovals;
        this.totalTransactions = totalTransactions;
        this.platformSavingsRate = platformSavingsRate;
    }

    public long getTotalUsers() { return totalUsers; }
    public long getTotalActiveUsers() { return totalActiveUsers; }
    public long getTotalLoans() { return totalLoans; }
    public long getTotalAccounts() { return totalAccounts; }
    public long getPendingApprovals() { return pendingApprovals; }
    public long getTotalTransactions() { return totalTransactions; }
    public double getPlatformSavingsRate() { return platformSavingsRate; }
}
