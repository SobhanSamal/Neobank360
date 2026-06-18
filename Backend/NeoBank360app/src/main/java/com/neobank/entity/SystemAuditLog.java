package com.neobank.entity;
 
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
 
import java.time.LocalDateTime;
 
@Entity
@Table(name = "system_audit_log")
public class SystemAuditLog {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @Column(nullable = false, length = 500)
    private String endpoint;
 
    @Column(name = "http_method", nullable = false, length = 10)
    private String httpMethod;
 
    @Column(name = "response_status", nullable = false)
    private Integer responseStatus;
 
    @Column(name = "execution_time_ms", nullable = false)
    private Long executionTimeMs;
 
    @Column(name = "acting_user_id")
    private Long actingUserId;
 
    @Column(name = "event_timestamp", nullable = false)
    private LocalDateTime eventTimestamp;
 
    @Column(name = "error_message", length = 1000)
    private String errorMessage;
 
    @PrePersist
    void onCreate() {
        if (eventTimestamp == null) {
            eventTimestamp = LocalDateTime.now();
        }
    }
 
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
    public String getHttpMethod() { return httpMethod; }
    public void setHttpMethod(String httpMethod) { this.httpMethod = httpMethod; }
    public Integer getResponseStatus() { return responseStatus; }
    public void setResponseStatus(Integer responseStatus) { this.responseStatus = responseStatus; }
    public Long getExecutionTimeMs() { return executionTimeMs; }
    public void setExecutionTimeMs(Long executionTimeMs) { this.executionTimeMs = executionTimeMs; }
    public Long getActingUserId() { return actingUserId; }
    public void setActingUserId(Long actingUserId) { this.actingUserId = actingUserId; }
    public LocalDateTime getEventTimestamp() { return eventTimestamp; }
    public void setEventTimestamp(LocalDateTime eventTimestamp) { this.eventTimestamp = eventTimestamp; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
 