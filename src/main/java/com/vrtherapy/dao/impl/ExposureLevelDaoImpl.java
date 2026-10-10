package com.vrtherapy.dao.impl;

import com.vrtherapy.dao.ExposureLevelDao;
import com.vrtherapy.exception.DatabaseException;
import com.vrtherapy.model.ExposureLevel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExposureLevelDaoImpl extends BaseJdbcDao implements ExposureLevelDao {

    @Override
    public Optional<ExposureLevel> findById(Long id) {
        String sql = "SELECT * FROM exposure_levels WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find exposure level: " + id, e);
        } finally {
            releaseConnection(conn);
        }
        return Optional.empty();
    }

    @Override
    public List<ExposureLevel> findAll() {
        String sql = "SELECT * FROM exposure_levels ORDER BY plan_id, level_order ASC";
        List<ExposureLevel> list = new ArrayList<>();
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch all exposure levels", e);
        } finally {
            releaseConnection(conn);
        }
        return list;
    }

    @Override
    public List<ExposureLevel> findByPlanIdOrderByLevelOrder(Long planId) {
        String sql = "SELECT * FROM exposure_levels WHERE plan_id = ? ORDER BY level_order ASC";
        List<ExposureLevel> list = new ArrayList<>();
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, planId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find exposure levels for plan: " + planId, e);
        } finally {
            releaseConnection(conn);
        }
        return list;
    }

    @Override
    public void unlockLevel(Long levelId) {
        String sql = "UPDATE exposure_levels SET is_unlocked = TRUE WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, levelId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to unlock exposure level: " + levelId, e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public ExposureLevel save(ExposureLevel entity) {
        String sql = "INSERT INTO exposure_levels (plan_id, session_id, level_order, title, description, distress_rating_threshold, is_unlocked, notes) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, entity.getPlanId());
            if (entity.getSessionId() != null) stmt.setLong(2, entity.getSessionId()); else stmt.setNull(2, java.sql.Types.BIGINT);
            stmt.setInt(3, entity.getLevelOrder());
            stmt.setString(4, entity.getTitle());
            stmt.setString(5, entity.getDescription());
            stmt.setInt(6, entity.getDistressRatingThreshold());
            stmt.setBoolean(7, entity.isUnlocked());
            stmt.setString(8, entity.getNotes());

            stmt.executeUpdate();
            Long generatedId = getGeneratedId(stmt);
            if (generatedId != null) {
                entity.setId(generatedId);
            }
            return entity;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save exposure level", e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public void update(ExposureLevel entity) {
        String sql = "UPDATE exposure_levels SET session_id = ?, level_order = ?, title = ?, description = ?, " +
                "distress_rating_threshold = ?, is_unlocked = ?, notes = ? WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (entity.getSessionId() != null) stmt.setLong(1, entity.getSessionId()); else stmt.setNull(1, java.sql.Types.BIGINT);
            stmt.setInt(2, entity.getLevelOrder());
            stmt.setString(3, entity.getTitle());
            stmt.setString(4, entity.getDescription());
            stmt.setInt(5, entity.getDistressRatingThreshold());
            stmt.setBoolean(6, entity.isUnlocked());
            stmt.setString(7, entity.getNotes());
            stmt.setLong(8, entity.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update exposure level: " + entity.getId(), e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM exposure_levels WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete exposure level: " + id, e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM exposure_levels";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count exposure levels", e);
        } finally {
            releaseConnection(conn);
        }
        return 0L;
    }

    private ExposureLevel mapRow(ResultSet rs) throws SQLException {
        ExposureLevel el = new ExposureLevel();
        el.setId(rs.getLong("id"));
        el.setPlanId(rs.getLong("plan_id"));
        long sessionId = rs.getLong("session_id");
        if (!rs.wasNull()) {
            el.setSessionId(sessionId);
        }
        el.setLevelOrder(rs.getInt("level_order"));
        el.setTitle(rs.getString("title"));
        el.setDescription(rs.getString("description"));
        el.setDistressRatingThreshold(rs.getInt("distress_rating_threshold"));
        el.setUnlocked(rs.getBoolean("is_unlocked"));
        el.setNotes(rs.getString("notes"));
        el.setCreatedAt(toLocalDateTime(rs.getTimestamp("created_at")));
        return el;
    }
}
