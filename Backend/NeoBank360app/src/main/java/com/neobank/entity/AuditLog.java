package com.neobank.entity;
 
import jakarta.persistence.*;
 
import java.time.LocalDateTime;
 
@Entity
@Table(name = "audit_log")
public class AuditLog {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @Column(name = "acting_admin_id", nullable = false)
    private Long actingAdminId;
 
    @Column(nullable = false, length = 100)
    private String action;
 
    @Column(name = "target_user_id")
    private Long targetUserId;
 
    @Column(nullable = false)
    private LocalDateTime createdAt;
 
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
 
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getActingAdminId() { return actingAdminId; }
    public void setActingAdminId(Long actingAdminId) { this.actingAdminId = actingAdminId; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public Long getTargetUserId() { return targetUserId; }
    public void setTargetUserId(Long targetUserId) { this.targetUserId = targetUserId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
 