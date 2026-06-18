package com.neobank.service;
 
import com.neobank.entity.SystemAuditLog;
import com.neobank.repository.SystemAuditLogRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
 
@Service
public class SystemAuditLogWriter {
    private final SystemAuditLogRepository repository;
 
    public SystemAuditLogWriter(SystemAuditLogRepository repository) {
        this.repository = repository;
    }
 
    @Async
    public void save(SystemAuditLog log) {
        repository.save(log);
    }
}
 