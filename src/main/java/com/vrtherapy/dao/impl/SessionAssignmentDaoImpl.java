package com.vrtherapy.dao.impl;

import com.vrtherapy.dao.SessionAssignmentDao;
import com.vrtherapy.exception.DatabaseException;
import com.vrtherapy.model.AssignmentStatus;
import com.vrtherapy.model.SessionAssignment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SessionAssignmentDaoImpl extends BaseJdbcDao implements SessionAssignmentDao {

    @Override
    public Optional<SessionAssignment> findById(Long id) {
        String sql = "SELECT * FROM session_assignments WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find assignment: " + id, e);
        } finally {
            releaseConnection(conn);
        }
        return Optional.empty();
    }

    @Override
    public List<SessionAssignment> findAll() {
        String sql = "SELECT * FROM session_assignments ORDER BY id DESC";
        List<SessionAssignment> list = new ArrayList<>();
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch all assignments", e);
        } finally {
            releaseConnection(conn);
        }
        return list;
    }

    @Override
    public List<SessionAssignment> findByPatientId(Long patientId) {
        String sql = "SELECT * FROM session_assignments WHERE patient_id = ? ORDER BY id DESC";
        List<SessionAssignment> list = new ArrayList<>();
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, patientId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find assignments for patient: " + patientId, e);
        } finally {
            releaseConnection(conn);
        }
        return list;
    }

    @Override
    public List<SessionAssignment> findByPatientIdAndStatus(Long patientId, AssignmentStatus status) {
        String sql = "SELECT * FROM session_assignments WHERE patient_id = ? AND status = ? ORDER BY id DESC";
        List<SessionAssignment> list = new ArrayList<>();
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, patientId);
            stmt.setString(2, status.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find assignments by status", e);
        } finally {
            releaseConnection(conn);
        }
        return list;
    }

    @Override
    public void updateStatus(Long assignmentId, AssignmentStatus status) {
        String sql = "UPDATE session_assignments SET status = ? WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setLong(2, assignmentId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update assignment status: " + assignmentId, e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public SessionAssignment save(SessionAssignment entity) {
        String sql = "INSERT INTO session_assignments (patient_id, session_id, plan_id, status, scheduled_date) VALUES (?, ?, ?, ?, ?)";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, entity.getPatientId());
            stmt.setLong(2, entity.getSessionId());
            if (entity.getPlanId() != null) stmt.setLong(3, entity.getPlanId()); else stmt.setNull(3, java.sql.Types.BIGINT);
            stmt.setString(4, entity.getStatus().name());
            stmt.setDate(5, toSqlDate(entity.getScheduledDate()));

            stmt.executeUpdate();
            Long generatedId = getGeneratedId(stmt);
            if (generatedId != null) {
                entity.setId(generatedId);
            }
            return entity;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save assignment", e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public void update(SessionAssignment entity) {
        String sql = "UPDATE session_assignments SET status = ?, scheduled_date = ? WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entity.getStatus().name());
            stmt.setDate(2, toSqlDate(entity.getScheduledDate()));
            stmt.setLong(3, entity.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update assignment: " + entity.getId(), e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM session_assignments WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete assignment: " + id, e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM session_assignments";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count assignments", e);
        } finally {
            releaseConnection(conn);
        }
        return 0L;
    }

    private SessionAssignment mapRow(ResultSet rs) throws SQLException {
        SessionAssignment sa = new SessionAssignment();
        sa.setId(rs.getLong("id"));
        sa.setPatientId(rs.getLong("patient_id"));
        sa.setSessionId(rs.getLong("session_id"));
        long planId = rs.getLong("plan_id");
        if (!rs.wasNull()) {
            sa.setPlanId(planId);
        }
        String statusStr = rs.getString("status");
        try {
            sa.setStatus(AssignmentStatus.valueOf(statusStr));
        } catch (Exception ignored) {
            sa.setStatus(AssignmentStatus.ASSIGNED);
        }
        sa.setScheduledDate(toLocalDate(rs.getDate("scheduled_date")));
        sa.setAssignedAt(toLocalDateTime(rs.getTimestamp("assigned_at")));
        return sa;
    }
}
