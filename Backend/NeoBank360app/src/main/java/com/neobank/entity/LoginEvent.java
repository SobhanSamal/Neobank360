package com.neobank.entity;
 
import jakarta.persistence.*;
 
import java.time.LocalDateTime;
 
@Entity
@Table(name = "login_events")
public class LoginEvent {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
 
    @Column(name = "login_at", nullable = false)
    private LocalDateTime loginAt;
 
    @Column(name = "ip_address", length = 64)
    private String ipAddress;
 
    @Column(name = "user_agent", length = 255)
    private String userAgent;
 
    @PrePersist
    protected void onCreate() {
        if (loginAt == null) {
            loginAt = LocalDateTime.now();
        }
    }
 
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public LocalDateTime getLoginAt() { return loginAt; }
    public void setLoginAt(LocalDateTime loginAt) { this.loginAt = loginAt; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
}
 