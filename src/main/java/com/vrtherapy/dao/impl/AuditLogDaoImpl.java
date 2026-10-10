package com.vrtherapy.dao.impl;

import com.vrtherapy.dao.AuditLogDao;
import com.vrtherapy.exception.DatabaseException;
import com.vrtherapy.model.AuditLog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AuditLogDaoImpl extends BaseJdbcDao implements AuditLogDao {

    @Override
    public Optional<AuditLog> findById(Long id) {
        String sql = "SELECT * FROM audit_logs WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find audit log: " + id, e);
        } finally {
            releaseConnection(conn);
        }
        return Optional.empty();
    }

    @Override
    public List<AuditLog> findRecent(int limit) {
        String sql = "SELECT * FROM audit_logs ORDER BY created_at DESC LIMIT ?";
        List<AuditLog> list = new ArrayList<>();
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find recent audit logs", e);
        } finally {
            releaseConnection(conn);
        }
        return list;
    }

    @Override
    public List<AuditLog> findByUserId(Long userId) {
        String sql = "SELECT * FROM audit_logs WHERE user_id = ? ORDER BY created_at DESC";
        List<AuditLog> list = new ArrayList<>();
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find audit logs for user: " + userId, e);
        } finally {
            releaseConnection(conn);
        }
        return list;
    }

    @Override
    public List<AuditLog> findAll() {
        return findRecent(100);
    }

    @Override
    public AuditLog save(AuditLog entity) {
        String sql = "INSERT INTO audit_logs (user_id, action, details, ip_address, created_at) VALUES (?, ?, ?, ?, ?)";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (entity.getUserId() != null) stmt.setLong(1, entity.getUserId()); else stmt.setNull(1, java.sql.Types.BIGINT);
            stmt.setString(2, entity.getAction());
            stmt.setString(3, entity.getDetails());
            stmt.setString(4, entity.getIpAddress());
            stmt.setTimestamp(5, toTimestamp(entity.getCreatedAt()));

            stmt.executeUpdate();
            Long generatedId = getGeneratedId(stmt);
            if (generatedId != null) {
                entity.setId(generatedId);
            }
            return entity;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save audit log", e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public void update(AuditLog entity) {
        // Audit logs are append-only immutable records
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM audit_logs WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete audit log: " + id, e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM audit_logs";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count audit logs", e);
        } finally {
            releaseConnection(conn);
        }
        return 0L;
    }

    private AuditLog mapRow(ResultSet rs) throws SQLException {
        AuditLog al = new AuditLog();
        al.setId(rs.getLong("id"));
        long uid = rs.getLong("user_id");
        if (!rs.wasNull()) al.setUserId(uid);
        al.setAction(rs.getString("action"));
        al.setDetails(rs.getString("details"));
        al.setIpAddress(rs.getString("ip_address"));
        al.setCreatedAt(toLocalDateTime(rs.getTimestamp("created_at")));
        return al;
    }
}
