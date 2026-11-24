package com.campusconnect.service;

import com.campusconnect.entity.AuditLog;
import com.campusconnect.entity.User;
import com.campusconnect.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Transactional
    public void logAction(User user, String action, String entityName, Long entityId, String details, String ipAddress) {
        AuditLog log = new AuditLog(user, action, entityName, entityId, details, ipAddress);
        auditLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAllByOrderByCreatedAtDesc();
    }
}
