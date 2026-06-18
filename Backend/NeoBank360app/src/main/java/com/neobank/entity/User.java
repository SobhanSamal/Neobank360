package com.neobank.entity;
 
import jakarta.persistence.*;
import java.time.LocalDateTime;
 
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_email", columnList = "email"),
    @Index(name = "idx_role", columnList = "role")
})
public class User {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @Column(name = "email", unique = true, nullable = false, length = 255)
    private String email;
 
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;
 
    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;
 
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;
 
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
 
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
 
    @Column(name = "default_account_id")
    private Long defaultAccountId;
 
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (role == null) {
            role = Role.CUSTOMER;
        }
        if (isActive == null) {
            isActive = true;
        }
    }
 
    public enum Role {
        ADMIN, CUSTOMER
    }
 
    public User() {
    }
 
    public User(Long id, String email, String passwordHash, String fullName, Role role, Boolean isActive, LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.role = role;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }
 
    public Long getId() {
        return id;
    }
 
    public void setId(Long id) {
        this.id = id;
    }
 
    public String getEmail() {
        return email;
    }
 
    public void setEmail(String email) {
        this.email = email;
    }
 
    public String getPasswordHash() {
        return passwordHash;
    }
 
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
 
    public String getFullName() {
        return fullName;
    }
 
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
 
    public Role getRole() {
        return role;
    }
 
    public void setRole(Role role) {
        this.role = role;
    }
 
    public Boolean getIsActive() {
        return isActive;
    }
 
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
 
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
 
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
 
    public Long getDefaultAccountId() {
        return defaultAccountId;
    }
 
    public void setDefaultAccountId(Long defaultAccountId) {
        this.defaultAccountId = defaultAccountId;
    }
 
}
 