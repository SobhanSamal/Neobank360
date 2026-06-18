package com.neobank.dto;

import com.neobank.entity.Reward;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RewardDTO {

    private Long userId;
    private BigDecimal pointsBalance;
    private LocalDateTime lastUpdated;

    public static RewardDTO from(Reward r) {
        RewardDTO dto = new RewardDTO();
        dto.setUserId(r.getUser().getId());
        dto.setPointsBalance(r.getPointsBalance());
        dto.setLastUpdated(r.getLastUpdated());
        return dto;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public BigDecimal getPointsBalance() { return pointsBalance; }
    public void setPointsBalance(BigDecimal pointsBalance) { this.pointsBalance = pointsBalance; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}
