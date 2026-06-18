package com.neobank.repository;
 
import com.neobank.entity.SystemAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
 
import java.time.LocalDateTime;
 
public interface SystemAuditLogRepository extends JpaRepository<SystemAuditLog, Long> {
    Page<SystemAuditLog> findByEventTimestampBetweenOrderByEventTimestampDesc(
        LocalDateTime from,
        LocalDateTime to,
        Pageable pageable
    );
 
    Page<SystemAuditLog> findByEventTimestampBetweenAndResponseStatusOrderByEventTimestampDesc(
        LocalDateTime from,
        LocalDateTime to,
        Integer status,
        Pageable pageable
    );
 
    Page<SystemAuditLog> findByEventTimestampBetweenAndResponseStatusGreaterThanEqualOrderByEventTimestampDesc(
            LocalDateTime from,
            LocalDateTime to,
            Integer status,
            Pageable pageable
    );
}
 