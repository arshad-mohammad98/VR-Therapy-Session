package com.vrtherapy.service;

import com.vrtherapy.model.AuditLog;
import java.util.List;

/**
 * Service contract managing administrative audit log entries and security event trails.
 */
public interface AuditLogService {

    /**
     * Records an audit log entry.
     */
    AuditLog recordAction(String username, String action, String details);

    default AuditLog logAction(String username, String action, String details) {
        return recordAction(username, action, details);
    }

    /**
     * Records an audit log entry with IP address tracking.
     */
    AuditLog recordAction(String username, String action, String details, String ipAddress);

    /**
     * Retrieves the most recent 100 audit log entries.
     */
    List<AuditLog> getRecentLogs();

    /**
     * Retrieves audit logs for a specific user.
     */
    List<AuditLog> getLogsByUser(String username);
}
