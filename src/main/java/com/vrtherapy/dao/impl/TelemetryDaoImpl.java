package com.vrtherapy.dao.impl;

import com.vrtherapy.dao.TelemetryDao;
import com.vrtherapy.exception.DatabaseException;
import com.vrtherapy.model.TelemetrySample;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Raw JDBC implementation of TelemetryDao demonstrating high-frequency batch inserts.
 *
 * Demonstrates:
 * - PreparedStatement.addBatch() and executeBatch()
 * - Time series statistical aggregation (average and peak metrics)
 */
public class TelemetryDaoImpl extends BaseJdbcDao implements TelemetryDao {

    @Override
    public Optional<TelemetrySample> findById(Long id) {
        String sql = "SELECT * FROM telemetry_samples WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find telemetry sample: " + id, e);
        } finally {
            releaseConnection(conn);
        }
        return Optional.empty();
    }

    @Override
    public List<TelemetrySample> findRecentByLiveSessionId(Long liveSessionId, int limit) {
        String sql = "SELECT * FROM telemetry_samples WHERE live_session_id = ? ORDER BY id DESC LIMIT ?";
        List<TelemetrySample> list = new ArrayList<>();
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, liveSessionId);
            stmt.setInt(2, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find telemetry for session: " + liveSessionId, e);
        } finally {
            releaseConnection(conn);
        }
        return list;
    }

    @Override
    public void batchInsert(List<TelemetrySample> samples) {
        if (samples == null || samples.isEmpty()) {
            return;
        }
        String sql = "INSERT INTO telemetry_samples (live_session_id, patient_id, heart_rate, stress_score, hrv, breathing_rate, recorded_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = getConnection();
        try {
            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (TelemetrySample sample : samples) {
                    if (sample.getLiveSessionId() != null) stmt.setLong(1, sample.getLiveSessionId()); else stmt.setNull(1, java.sql.Types.BIGINT);
                    if (sample.getPatientId() != null) stmt.setLong(2, sample.getPatientId()); else stmt.setNull(2, java.sql.Types.BIGINT);
                    stmt.setInt(3, sample.getHeartRate());
                    stmt.setInt(4, sample.getStressScore());
                    stmt.setInt(5, sample.getHrv());
                    stmt.setInt(6, sample.getBreathingRate());
                    stmt.setTimestamp(7, toTimestamp(sample.getRecordedAt()));
                    stmt.addBatch();
                }
                stmt.executeBatch();
                conn.commit();
            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(originalAutoCommit);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to execute telemetry batch insert", e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public double calculateAverageHeartRate(Long liveSessionId) {
        String sql = "SELECT AVG(heart_rate) FROM telemetry_samples WHERE live_session_id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, liveSessionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to compute average heart rate", e);
        } finally {
            releaseConnection(conn);
        }
        return 75.0;
    }

    @Override
    public int findPeakHeartRate(Long liveSessionId) {
        String sql = "SELECT MAX(heart_rate) FROM telemetry_samples WHERE live_session_id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, liveSessionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find peak heart rate", e);
        } finally {
            releaseConnection(conn);
        }
        return 85;
    }

    @Override
    public List<TelemetrySample> findAll() {
        String sql = "SELECT * FROM telemetry_samples ORDER BY id DESC LIMIT 100";
        List<TelemetrySample> list = new ArrayList<>();
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch telemetry samples", e);
        } finally {
            releaseConnection(conn);
        }
        return list;
    }

    @Override
    public TelemetrySample save(TelemetrySample entity) {
        String sql = "INSERT INTO telemetry_samples (live_session_id, patient_id, heart_rate, stress_score, hrv, breathing_rate, recorded_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (entity.getLiveSessionId() != null) stmt.setLong(1, entity.getLiveSessionId()); else stmt.setNull(1, java.sql.Types.BIGINT);
            if (entity.getPatientId() != null) stmt.setLong(2, entity.getPatientId()); else stmt.setNull(2, java.sql.Types.BIGINT);
            stmt.setInt(3, entity.getHeartRate());
            stmt.setInt(4, entity.getStressScore());
            stmt.setInt(5, entity.getHrv());
            stmt.setInt(6, entity.getBreathingRate());
            stmt.setTimestamp(7, toTimestamp(entity.getRecordedAt()));

            stmt.executeUpdate();
            Long generatedId = getGeneratedId(stmt);
            if (generatedId != null) {
                entity.setId(generatedId);
            }
            return entity;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save telemetry sample", e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public void update(TelemetrySample entity) {
        // Telemetry readings are immutable time-series data
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM telemetry_samples WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete telemetry sample: " + id, e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM telemetry_samples";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count telemetry samples", e);
        } finally {
            releaseConnection(conn);
        }
        return 0L;
    }

    private TelemetrySample mapRow(ResultSet rs) throws SQLException {
        TelemetrySample s = new TelemetrySample();
        s.setId(rs.getLong("id"));
        long liveId = rs.getLong("live_session_id");
        if (!rs.wasNull()) s.setLiveSessionId(liveId);
        long patId = rs.getLong("patient_id");
        if (!rs.wasNull()) s.setPatientId(patId);
        s.setHeartRate(rs.getInt("heart_rate"));
        s.setStressScore(rs.getInt("stress_score"));
        s.setHrv(rs.getInt("hrv"));
        s.setBreathingRate(rs.getInt("breathing_rate"));
        s.setRecordedAt(toLocalDateTime(rs.getTimestamp("recorded_at")));
        return s;
    }
}
