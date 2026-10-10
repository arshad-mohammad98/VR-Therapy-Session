package com.vrtherapy.dao.impl;

import com.vrtherapy.dao.SessionDao;
import com.vrtherapy.exception.DatabaseException;
import com.vrtherapy.model.ConditionType;
import com.vrtherapy.model.EnvironmentType;
import com.vrtherapy.model.TherapySession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SessionDaoImpl extends BaseJdbcDao implements SessionDao {

    @Override
    public Optional<TherapySession> findById(Long id) {
        String sql = "SELECT * FROM therapy_sessions WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find session by ID: " + id, e);
        } finally {
            releaseConnection(conn);
        }
        return Optional.empty();
    }

    @Override
    public List<TherapySession> findAll() {
        String sql = "SELECT * FROM therapy_sessions ORDER BY id ASC";
        List<TherapySession> list = new ArrayList<>();
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find all sessions", e);
        } finally {
            releaseConnection(conn);
        }
        return list;
    }

    @Override
    public List<TherapySession> findByCreatedBy(Long createdBy) {
        String sql = "SELECT * FROM therapy_sessions WHERE created_by = ? ORDER BY id ASC";
        List<TherapySession> list = new ArrayList<>();
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, createdBy);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find sessions by creator: " + createdBy, e);
        } finally {
            releaseConnection(conn);
        }
        return list;
    }

    @Override
    public List<TherapySession> findByConditionType(ConditionType conditionType) {
        String sql = "SELECT * FROM therapy_sessions WHERE condition_type = ? ORDER BY id ASC";
        List<TherapySession> list = new ArrayList<>();
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, conditionType.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find sessions by condition: " + conditionType, e);
        } finally {
            releaseConnection(conn);
        }
        return list;
    }

    @Override
    public TherapySession save(TherapySession entity) {
        String sql = "INSERT INTO therapy_sessions (title, condition_type, vr_environment, default_duration_minutes, " +
                "intensity_level, audio_enabled, guided_breathing_enabled, therapist_notes, config_json, created_by) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, entity.getTitle());
            stmt.setString(2, entity.getConditionType().name());
            stmt.setString(3, entity.getVrEnvironment().name());
            stmt.setInt(4, entity.getDefaultDurationMinutes());
            stmt.setInt(5, entity.getIntensityLevel());
            stmt.setBoolean(6, entity.isAudioEnabled());
            stmt.setBoolean(7, entity.isGuidedBreathingEnabled());
            stmt.setString(8, entity.getTherapistNotes());
            stmt.setString(9, entity.getConfigJson());
            if (entity.getCreatedBy() != null) stmt.setLong(10, entity.getCreatedBy()); else stmt.setNull(10, java.sql.Types.BIGINT);

            stmt.executeUpdate();
            Long generatedId = getGeneratedId(stmt);
            if (generatedId != null) {
                entity.setId(generatedId);
            }
            return entity;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save therapy session", e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public void update(TherapySession entity) {
        String sql = "UPDATE therapy_sessions SET title = ?, condition_type = ?, vr_environment = ?, " +
                "default_duration_minutes = ?, intensity_level = ?, audio_enabled = ?, guided_breathing_enabled = ?, " +
                "therapist_notes = ?, config_json = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entity.getTitle());
            stmt.setString(2, entity.getConditionType().name());
            stmt.setString(3, entity.getVrEnvironment().name());
            stmt.setInt(4, entity.getDefaultDurationMinutes());
            stmt.setInt(5, entity.getIntensityLevel());
            stmt.setBoolean(6, entity.isAudioEnabled());
            stmt.setBoolean(7, entity.isGuidedBreathingEnabled());
            stmt.setString(8, entity.getTherapistNotes());
            stmt.setString(9, entity.getConfigJson());
            stmt.setLong(10, entity.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update therapy session: " + entity.getId(), e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM therapy_sessions WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete therapy session: " + id, e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM therapy_sessions";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count sessions", e);
        } finally {
            releaseConnection(conn);
        }
        return 0L;
    }

    private TherapySession mapRow(ResultSet rs) throws SQLException {
        TherapySession session = new TherapySession();
        session.setId(rs.getLong("id"));
        session.setTitle(rs.getString("title"));

        String condStr = rs.getString("condition_type");
        try {
            session.setConditionType(ConditionType.valueOf(condStr.toUpperCase().replace(" ", "_")));
        } catch (Exception ignored) {
            session.setConditionType(ConditionType.ANXIETY);
        }

        String envStr = rs.getString("vr_environment");
        try {
            session.setVrEnvironment(EnvironmentType.valueOf(envStr.toUpperCase().replace(" ", "_")));
        } catch (Exception ignored) {
            session.setVrEnvironment(EnvironmentType.CALM_BEACH);
        }

        session.setDefaultDurationMinutes(rs.getInt("default_duration_minutes"));
        session.setIntensityLevel(rs.getInt("intensity_level"));
        session.setAudioEnabled(rs.getBoolean("audio_enabled"));
        session.setGuidedBreathingEnabled(rs.getBoolean("guided_breathing_enabled"));
        session.setTherapistNotes(rs.getString("therapist_notes"));
        session.setConfigJson(rs.getString("config_json"));

        long createdBy = rs.getLong("created_by");
        if (!rs.wasNull()) {
            session.setCreatedBy(createdBy);
        }

        session.setCreatedAt(toLocalDateTime(rs.getTimestamp("created_at")));
        session.setUpdatedAt(toLocalDateTime(rs.getTimestamp("updated_at")));
        return session;
    }
}
