package com.vrtherapy.dao.impl;

import com.vrtherapy.dao.SessionResultDao;
import com.vrtherapy.exception.DatabaseException;
import com.vrtherapy.model.SessionResult;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SessionResultDaoImpl extends BaseJdbcDao implements SessionResultDao {

    @Override
    public Optional<SessionResult> findById(Long id) {
        String sql = "SELECT * FROM session_results WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find session result by ID: " + id, e);
        } finally {
            releaseConnection(conn);
        }
        return Optional.empty();
    }

    @Override
    public List<SessionResult> findAll() {
        String sql = "SELECT * FROM session_results ORDER BY id DESC";
        List<SessionResult> list = new ArrayList<>();
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch all session results", e);
        } finally {
            releaseConnection(conn);
        }
        return list;
    }

    @Override
    public List<SessionResult> findByPatientId(Long patientId) {
        String sql = "SELECT * FROM session_results WHERE patient_id = ? ORDER BY completed_at DESC";
        List<SessionResult> list = new ArrayList<>();
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, patientId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find results for patient: " + patientId, e);
        } finally {
            releaseConnection(conn);
        }
        return list;
    }

    @Override
    public List<SessionResult> findBySessionId(Long sessionId) {
        String sql = "SELECT * FROM session_results WHERE session_id = ? ORDER BY completed_at DESC";
        List<SessionResult> list = new ArrayList<>();
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, sessionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find results for session: " + sessionId, e);
        } finally {
            releaseConnection(conn);
        }
        return list;
    }

    @Override
    public List<SessionResult> findRecentByPatientId(Long patientId, int limit) {
        String sql = "SELECT * FROM session_results WHERE patient_id = ? ORDER BY completed_at DESC LIMIT ?";
        List<SessionResult> list = new ArrayList<>();
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, patientId);
            stmt.setInt(2, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find recent results for patient", e);
        } finally {
            releaseConnection(conn);
        }
        return list;
    }

    @Override
    public SessionResult save(SessionResult entity) {
        String sql = "INSERT INTO session_results (session_id, patient_id, pre_stress_score, post_stress_score, " +
                "duration_seconds, average_heart_rate, peak_heart_rate, emergency_stopped, stop_reason, completion_notes, completed_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, entity.getSessionId());
            stmt.setLong(2, entity.getPatientId());
            stmt.setInt(3, entity.getPreStressScore());
            stmt.setInt(4, entity.getPostStressScore());
            stmt.setInt(5, entity.getDurationSeconds());
            stmt.setInt(6, entity.getAverageHeartRate());
            stmt.setInt(7, entity.getPeakHeartRate());
            stmt.setBoolean(8, entity.isEmergencyStopped());
            stmt.setString(9, entity.getStopReason());
            stmt.setString(10, entity.getCompletionNotes());
            stmt.setTimestamp(11, toTimestamp(entity.getCompletedAt()));

            stmt.executeUpdate();
            Long generatedId = getGeneratedId(stmt);
            if (generatedId != null) {
                entity.setId(generatedId);
            }
            return entity;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save session result", e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public void update(SessionResult entity) {
        String sql = "UPDATE session_results SET completion_notes = ?, stop_reason = ? WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entity.getCompletionNotes());
            stmt.setString(2, entity.getStopReason());
            stmt.setLong(3, entity.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update session result: " + entity.getId(), e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM session_results WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete session result: " + id, e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM session_results";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count session results", e);
        } finally {
            releaseConnection(conn);
        }
        return 0L;
    }

    private SessionResult mapRow(ResultSet rs) throws SQLException {
        SessionResult res = new SessionResult();
        res.setId(rs.getLong("id"));
        res.setSessionId(rs.getLong("session_id"));
        res.setPatientId(rs.getLong("patient_id"));
        res.setPreStressScore(rs.getInt("pre_stress_score"));
        res.setPostStressScore(rs.getInt("post_stress_score"));
        res.setDurationSeconds(rs.getInt("duration_seconds"));
        res.setAverageHeartRate(rs.getInt("average_heart_rate"));
        res.setPeakHeartRate(rs.getInt("peak_heart_rate"));
        res.setEmergencyStopped(rs.getBoolean("emergency_stopped"));
        res.setStopReason(rs.getString("stop_reason"));
        res.setCompletionNotes(rs.getString("completion_notes"));
        res.setCompletedAt(toLocalDateTime(rs.getTimestamp("completed_at")));
        return res;
    }
}
