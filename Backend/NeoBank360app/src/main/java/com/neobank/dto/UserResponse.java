package com.neobank.dto;
 
import com.neobank.entity.User;
import java.time.LocalDateTime;
 
public class UserResponse {
    private Long id;
    private String email;
    private String fullName;
    private String role;
    private Boolean active;
    private LocalDateTime createdAt;
    private Long defaultAccountId;
 
    public UserResponse() {
    }
 
    public UserResponse(Long id, String email, String fullName, String role, Boolean active, LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.active = active;
        this.createdAt = createdAt;
    }
 
    public static UserResponse fromEntity(User user) {
        UserResponse response = new UserResponse(
            user.getId(),
            user.getEmail(),
            user.getFullName(),
            user.getRole().name(),
            user.getIsActive(),
            user.getCreatedAt()
        );
        response.setDefaultAccountId(user.getDefaultAccountId());
        return response;
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
 
    public String getFullName() {
        return fullName;
    }
 
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
 
    public String getRole() {
        return role;
    }
 
    public void setRole(String role) {
        this.role = role;
    }
 
    public Boolean getActive() {
        return active;
    }
 
    public void setActive(Boolean active) {
        this.active = active;
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
 