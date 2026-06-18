package com.neobank.repository;
 
import com.neobank.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
 
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
 