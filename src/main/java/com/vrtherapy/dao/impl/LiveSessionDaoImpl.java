package com.vrtherapy.dao.impl;

import com.vrtherapy.dao.LiveSessionDao;
import com.vrtherapy.exception.DatabaseException;
import com.vrtherapy.model.EnvironmentType;
import com.vrtherapy.model.LiveSession;
import com.vrtherapy.model.LiveSessionState;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LiveSessionDaoImpl extends BaseJdbcDao implements LiveSessionDao {

    @Override
    public Optional<LiveSession> findById(Long id) {
        String sql = "SELECT * FROM live_sessions WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find live session by ID: " + id, e);
        } finally {
            releaseConnection(conn);
        }
        return Optional.empty();
    }

    @Override
    public Optional<LiveSession> findActiveByPatientId(Long patientId) {
        String sql = "SELECT * FROM live_sessions WHERE patient_id = ? AND state IN ('WAITING', 'ACTIVE', 'PAUSED') ORDER BY id DESC LIMIT 1";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, patientId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find active live session for patient: " + patientId, e);
        } finally {
            releaseConnection(conn);
        }
        return Optional.empty();
    }

    @Override
    public Optional<LiveSession> findActiveByTherapistId(Long therapistId) {
        String sql = "SELECT * FROM live_sessions WHERE therapist_id = ? AND state IN ('WAITING', 'ACTIVE', 'PAUSED') ORDER BY id DESC LIMIT 1";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, therapistId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find active live session for therapist: " + therapistId, e);
        } finally {
            releaseConnection(conn);
        }
        return Optional.empty();
    }

    @Override
    public List<LiveSession> findByState(LiveSessionState state) {
        String sql = "SELECT * FROM live_sessions WHERE state = ? ORDER BY id DESC";
        List<LiveSession> list = new ArrayList<>();
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, state.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find live sessions by state: " + state, e);
        } finally {
            releaseConnection(conn);
        }
        return list;
    }

    @Override
    public void updateLiveParameters(Long liveSessionId, int heartRate, int stressScore, int elapsedSeconds) {
        String sql = "UPDATE live_sessions SET current_heart_rate = ?, current_stress_score = ?, elapsed_seconds = ? WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, heartRate);
            stmt.setInt(2, stressScore);
            stmt.setInt(3, elapsedSeconds);
            stmt.setLong(4, liveSessionId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update live parameters for session: " + liveSessionId, e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public void updateState(Long liveSessionId, LiveSessionState state) {
        String sql = "UPDATE live_sessions SET state = ?, " +
                "started_at = CASE WHEN ? = 'ACTIVE' AND started_at IS NULL THEN CURRENT_TIMESTAMP ELSE started_at END, " +
                "ended_at = CASE WHEN ? IN ('COMPLETED', 'EMERGENCY_STOPPED') THEN CURRENT_TIMESTAMP ELSE ended_at END " +
                "WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, state.name());
            stmt.setString(2, state.name());
            stmt.setString(3, state.name());
            stmt.setLong(4, liveSessionId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update state for live session: " + liveSessionId, e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public void updateEnvironmentAndIntensity(Long liveSessionId, String environment, int intensity, boolean breathingGuide) {
        String sql = "UPDATE live_sessions SET current_environment = ?, current_intensity = ?, breathing_guide_active = ? WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, environment);
            stmt.setInt(2, intensity);
            stmt.setBoolean(3, breathingGuide);
            stmt.setLong(4, liveSessionId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update environment/intensity for session: " + liveSessionId, e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public void updateInVrMessage(Long liveSessionId, String message) {
        String sql = "UPDATE live_sessions SET in_vr_message = ? WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, message);
            stmt.setLong(2, liveSessionId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update in-VR message for session: " + liveSessionId, e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public List<LiveSession> findAll() {
        String sql = "SELECT * FROM live_sessions ORDER BY id DESC";
        List<LiveSession> list = new ArrayList<>();
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch all live sessions", e);
        } finally {
            releaseConnection(conn);
        }
        return list;
    }

    @Override
    public LiveSession save(LiveSession entity) {
        String sql = "INSERT INTO live_sessions (session_id, patient_id, therapist_id, state, current_environment, " +
                "current_intensity, breathing_guide_active, elapsed_seconds, current_heart_rate, current_stress_score, in_vr_message) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, entity.getSessionId());
            stmt.setLong(2, entity.getPatientId());
            stmt.setLong(3, entity.getTherapistId());
            stmt.setString(4, entity.getState().name());
            stmt.setString(5, entity.getCurrentEnvironment().name());
            stmt.setInt(6, entity.getCurrentIntensity());
            stmt.setBoolean(7, entity.isBreathingGuideActive());
            stmt.setInt(8, entity.getElapsedSeconds());
            stmt.setInt(9, entity.getCurrentHeartRate());
            stmt.setInt(10, entity.getCurrentStressScore());
            stmt.setString(11, entity.getInVrMessage());

            stmt.executeUpdate();
            Long generatedId = getGeneratedId(stmt);
            if (generatedId != null) {
                entity.setId(generatedId);
            }
            return entity;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save live session", e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public void update(LiveSession entity) {
        String sql = "UPDATE live_sessions SET state = ?, current_environment = ?, current_intensity = ?, " +
                "breathing_guide_active = ?, elapsed_seconds = ?, current_heart_rate = ?, current_stress_score = ?, " +
                "in_vr_message = ? WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entity.getState().name());
            stmt.setString(2, entity.getCurrentEnvironment().name());
            stmt.setInt(3, entity.getCurrentIntensity());
            stmt.setBoolean(4, entity.isBreathingGuideActive());
            stmt.setInt(5, entity.getElapsedSeconds());
            stmt.setInt(6, entity.getCurrentHeartRate());
            stmt.setInt(7, entity.getCurrentStressScore());
            stmt.setString(8, entity.getInVrMessage());
            stmt.setLong(9, entity.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update live session: " + entity.getId(), e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM live_sessions WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete live session: " + id, e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM live_sessions";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count live sessions", e);
        } finally {
            releaseConnection(conn);
        }
        return 0L;
    }

    private LiveSession mapRow(ResultSet rs) throws SQLException {
        LiveSession ls = new LiveSession();
        ls.setId(rs.getLong("id"));
        ls.setSessionId(rs.getLong("session_id"));
        ls.setPatientId(rs.getLong("patient_id"));
        ls.setTherapistId(rs.getLong("therapist_id"));

        String stateStr = rs.getString("state");
        try {
            ls.setState(LiveSessionState.valueOf(stateStr));
        } catch (Exception ignored) {
            ls.setState(LiveSessionState.SCHEDULED);
        }

        String envStr = rs.getString("current_environment");
        try {
            ls.setCurrentEnvironment(EnvironmentType.valueOf(envStr.toUpperCase().replace(" ", "_")));
        } catch (Exception ignored) {
            ls.setCurrentEnvironment(EnvironmentType.CALM_BEACH);
        }

        ls.setCurrentIntensity(rs.getInt("current_intensity"));
        ls.setBreathingGuideActive(rs.getBoolean("breathing_guide_active"));
        ls.setElapsedSeconds(rs.getInt("elapsed_seconds"));
        ls.setCurrentHeartRate(rs.getInt("current_heart_rate"));
        ls.setCurrentStressScore(rs.getInt("current_stress_score"));
        ls.setInVrMessage(rs.getString("in_vr_message"));
        ls.setStartedAt(toLocalDateTime(rs.getTimestamp("started_at")));
        ls.setEndedAt(toLocalDateTime(rs.getTimestamp("ended_at")));
        ls.setCreatedAt(toLocalDateTime(rs.getTimestamp("created_at")));
        return ls;
    }
}
