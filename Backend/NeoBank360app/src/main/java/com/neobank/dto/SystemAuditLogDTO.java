package com.neobank.dto;
 
import java.time.LocalDateTime;
 
public class SystemAuditLogDTO {
    private Long id;
    private String endpoint;
    private String httpMethod;
    private Integer responseStatus;
    private Long executionTimeMs;
    private Long actingUserId;
    private LocalDateTime eventTimestamp;
    private String errorMessage;
 
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
 