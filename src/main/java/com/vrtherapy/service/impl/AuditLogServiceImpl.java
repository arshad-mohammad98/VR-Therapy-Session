package com.vrtherapy.service.impl;

import com.vrtherapy.dao.AuditLogDao;
import com.vrtherapy.dao.UserDao;
import com.vrtherapy.dao.impl.AuditLogDaoImpl;
import com.vrtherapy.dao.impl.UserDaoImpl;
import com.vrtherapy.model.AuditLog;
import com.vrtherapy.model.User;
import com.vrtherapy.service.AuditLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class AuditLogServiceImpl implements AuditLogService {

    private static final Logger logger = LoggerFactory.getLogger(AuditLogServiceImpl.class);

    private final AuditLogDao auditLogDao;
    private final UserDao userDao;

    public AuditLogServiceImpl() {
        this(new AuditLogDaoImpl(), new UserDaoImpl());
    }

    public AuditLogServiceImpl(AuditLogDao auditLogDao, UserDao userDao) {
        this.auditLogDao = auditLogDao;
        this.userDao = userDao;
    }

    @Override
    public AuditLog recordAction(String username, String action, String details) {
        return recordAction(username, action, details, "127.0.0.1");
    }

    @Override
    public AuditLog recordAction(String username, String action, String details, String ipAddress) {
        Long userId = null;
        if (username != null) {
            Optional<User> opt = userDao.findByUsername(username);
            if (opt.isPresent()) {
                userId = opt.get().getId();
            }
        }

        AuditLog log = new AuditLog(userId, action, details, ipAddress);
        AuditLog saved = auditLogDao.save(log);
        logger.info("[AUDIT] User '{}' Action '{}' - {}", username, action, details);
        return saved;
    }

    @Override
    public List<AuditLog> getRecentLogs() {
        return auditLogDao.findRecent(100);
    }

    @Override
    public List<AuditLog> getLogsByUser(String username) {
        if (username == null) {
            return List.of();
        }
        Optional<User> opt = userDao.findByUsername(username);
        return opt.map(user -> auditLogDao.findByUserId(user.getId())).orElseGet(List::of);
    }
}
