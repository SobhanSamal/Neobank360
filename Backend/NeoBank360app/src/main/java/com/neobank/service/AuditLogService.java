package com.neobank.service;
 
import com.neobank.entity.AuditLog;
import com.neobank.repository.AuditLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
@Service
public class AuditLogService {
 
    private final AuditLogRepository auditLogRepository;
 
    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }
 
    @Transactional
    public void log(Long actingAdminId, String action, Long targetUserId) {
        AuditLog log = new AuditLog();
        log.setActingAdminId(actingAdminId);
        log.setAction(action);
        log.setTargetUserId(targetUserId);
        auditLogRepository.save(log);
    }
}
 