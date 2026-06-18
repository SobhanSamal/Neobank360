package com.neobank.repository;

import com.neobank.entity.LoginEvent;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface LoginEventRepository extends JpaRepository<LoginEvent, Long> {

    // ✅ EXISTING (KEEP)
    List<LoginEvent> findTop5ByUser_IdOrderByLoginAtDesc(Long userId);

    // ✅ ADD THIS (FOR ACTIVE SESSIONS 🔥)
    @Query("""
        SELECT COUNT(l)
        FROM LoginEvent l
        WHERE l.loginAt >= :time
    """)
    long countActiveSessions(@Param("time") LocalDateTime time);
}
