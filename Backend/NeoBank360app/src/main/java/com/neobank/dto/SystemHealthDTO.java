package com.neobank.dto;

public class SystemHealthDTO {

    private String dbStatus;
    private long activeUsers;
    private long totalUsers;
    private long totalAccounts;
    private long totalTransactions;
    private long uptime;
    private long activeSessions;

    public SystemHealthDTO(String dbStatus,
                           long activeUsers,
                           long totalUsers,
                           long totalAccounts,
                           long totalTransactions,
                           long uptime,
                           long activeSessions) {
        this.dbStatus = dbStatus;
        this.activeUsers = activeUsers;
        this.totalUsers = totalUsers;
        this.totalAccounts = totalAccounts;
        this.totalTransactions = totalTransactions;
        this.uptime = uptime;
        this.activeSessions = activeSessions;
    }

    public String getDbStatus() { return dbStatus; }
    public long getActiveUsers() { return activeUsers; }
    public long getTotalUsers() { return totalUsers; }
    public long getTotalAccounts() { return totalAccounts; }
    public long getTotalTransactions() { return totalTransactions; }
    public long getUptime() { return uptime; }
    public long getActiveSessions() { return activeSessions; }
}
