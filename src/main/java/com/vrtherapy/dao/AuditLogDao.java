package com.vrtherapy.dao;

import com.vrtherapy.model.AuditLog;

import java.util.List;

public interface AuditLogDao extends GenericDao<AuditLog, Long> {

    List<AuditLog> findRecent(int limit);

    List<AuditLog> findByUserId(Long userId);
}
