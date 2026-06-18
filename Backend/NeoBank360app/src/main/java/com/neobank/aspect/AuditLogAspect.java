package com.neobank.aspect;
 
import com.neobank.entity.SystemAuditLog;
import com.neobank.repository.UserRepository;
import com.neobank.service.SystemAuditLogWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
 
@Aspect
@Component
public class AuditLogAspect {
 
    private final SystemAuditLogWriter auditLogWriter;
    private final UserRepository userRepository;
 
    public AuditLogAspect(SystemAuditLogWriter auditLogWriter, UserRepository userRepository) {
        this.auditLogWriter = auditLogWriter;
        this.userRepository = userRepository;
    }
 
    @Around("execution(* com.neobank.controller..*(..))")
    public Object logExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        HttpServletRequest request = currentRequest();
        SystemAuditLog log = new SystemAuditLog();
 
        if (request != null) {
            log.setEndpoint(request.getRequestURI());
            log.setHttpMethod(request.getMethod());
        } else {
            log.setEndpoint(joinPoint.getSignature().toShortString());
            log.setHttpMethod("UNKNOWN");
        }
 
        try {
            Object result = joinPoint.proceed();
            log.setResponseStatus(currentResponseStatus());
            return result;
        } catch (Throwable ex) {
            log.setResponseStatus(currentResponseStatus() >= 400 ? currentResponseStatus() : 500);
            log.setErrorMessage(safeMessage(ex));
            throw ex;
        } finally {
            log.setExecutionTimeMs(System.currentTimeMillis() - start);
            log.setActingUserId(currentUserId());
            auditLogWriter.save(log);
        }
    }
 
    private HttpServletRequest currentRequest() {
        var attrs = RequestContextHolder.getRequestAttributes();
        if (attrs instanceof ServletRequestAttributes servletRequestAttributes) {
            return servletRequestAttributes.getRequest();
        }
        return null;
    }
 
    private int currentResponseStatus() {
        var attrs = RequestContextHolder.getRequestAttributes();
        if (attrs instanceof ServletRequestAttributes servletRequestAttributes) {
            HttpServletResponse response = servletRequestAttributes.getResponse();
            if (response != null) {
                return response.getStatus();
            }
        }
        return 200;
    }
 
    private Long currentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getName() == null) {
            return null;
        }
        return userRepository.findByEmail(authentication.getName()).map(user -> user.getId()).orElse(null);
    }
 
    private String safeMessage(Throwable ex) {
        String message = ex.getMessage();
        if (message == null) {
            return ex.getClass().getSimpleName();
        }
        String lower = message.toLowerCase();
        if (lower.contains("password") || lower.contains("jwt") || lower.contains("token") || lower.contains("pii")) {
            return ex.getClass().getSimpleName();
        }
        return message.length() > 1000 ? message.substring(0, 1000) : message;
    }
}
 