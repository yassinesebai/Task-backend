package com.task.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.task.backend.model.AuditTrail;
import com.task.backend.repository.AuditTrailRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LogAuditService {
    private static final Logger log = LoggerFactory.getLogger(LogAuditService.class);
    private final AuditTrailRepository auditTrailRepository;

    public void logAuditTrail(String action, String entityName, String entityId, Object oldValue, Object newValue) {
        AuditTrail audit = new AuditTrail();
        audit.setAction(action);
        audit.setEntityName(entityName);
        audit.setEntityId(entityId);
        audit.setModifiedBy(getCurrentUser());
        audit.setTimestamp(LocalDateTime.now());
        audit.setChanges(constructChangeLog(oldValue, newValue));

        auditTrailRepository.save(audit);
    }

    private String constructChangeLog(Object oldValue, Object newValue) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            Map<String, Object> changes = new HashMap<>();
            if (oldValue != null) {
                changes.put("oldValue", objectMapper.writeValueAsString(oldValue));
            }
            if (newValue != null) {
                changes.put("newValue", objectMapper.writeValueAsString(newValue));
            }
            return objectMapper.writeValueAsString(changes);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Failed to log changes{}", e.getMessage());
            return "Failed to log changes - " + e.getMessage();
        }
    }

    private String getCurrentUser() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
    }
}
