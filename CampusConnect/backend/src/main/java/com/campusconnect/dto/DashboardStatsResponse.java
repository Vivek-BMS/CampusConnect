package com.campusconnect.dto;

import java.util.Map;

public class DashboardStatsResponse {
    private long totalLost;
    private long totalFound;
    private long potentialMatches;
    private long recoveredItems;
    private long pendingClaims;
    private long totalUsers;
    private Map<String, Long> categoryDistribution;
    private Map<String, Long> monthlyRecovery;

    public DashboardStatsResponse() {}

    // Getters and setters
    public long getTotalLost() { return totalLost; }
    public void setTotalLost(long totalLost) { this.totalLost = totalLost; }

    public long getTotalFound() { return totalFound; }
    public void setTotalFound(long totalFound) { this.totalFound = totalFound; }

    public long getPotentialMatches() { return potentialMatches; }
    public void setPotentialMatches(long potentialMatches) { this.potentialMatches = potentialMatches; }

    public long getRecoveredItems() { return recoveredItems; }
    public void setRecoveredItems(long recoveredItems) { this.recoveredItems = recoveredItems; }

    public long getPendingClaims() { return pendingClaims; }
    public void setPendingClaims(long pendingClaims) { this.pendingClaims = pendingClaims; }

    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }

    public Map<String, Long> getCategoryDistribution() { return categoryDistribution; }
    public void setCategoryDistribution(Map<String, Long> categoryDistribution) { this.categoryDistribution = categoryDistribution; }

    public Map<String, Long> getMonthlyRecovery() { return monthlyRecovery; }
    public void setMonthlyRecovery(Map<String, Long> monthlyRecovery) { this.monthlyRecovery = monthlyRecovery; }
}
