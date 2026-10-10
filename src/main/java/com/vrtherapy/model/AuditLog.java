package com.vrtherapy.model;

import java.time.LocalDateTime;

/**
 * Domain entity recording security-relevant actions for audit compliance.
 */
public class AuditLog extends BaseEntity {

    private Long userId;
    private String action;
    private String details;
    private String ipAddress;
    private LocalDateTime createdAt;

    public AuditLog() {
        super();
        this.createdAt = LocalDateTime.now();
    }

    public AuditLog(Long userId, String action, String details, String ipAddress) {
        super();
        this.userId = userId;
        this.action = action;
        this.details = details;
        this.ipAddress = ipAddress;
        this.createdAt = LocalDateTime.now();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
