package com.vrtherapy.dao.impl;

import com.vrtherapy.dao.BadgeDao;
import com.vrtherapy.exception.DatabaseException;
import com.vrtherapy.model.Badge;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BadgeDaoImpl extends BaseJdbcDao implements BadgeDao {

    @Override
    public Optional<Badge> findById(Long id) {
        String sql = "SELECT * FROM badges WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find badge: " + id, e);
        } finally {
            releaseConnection(conn);
        }
        return Optional.empty();
    }

    @Override
    public List<Badge> findByPatientId(Long patientId) {
        String sql = "SELECT * FROM badges WHERE patient_id = ? ORDER BY awarded_at DESC";
        List<Badge> list = new ArrayList<>();
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, patientId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find badges for patient: " + patientId, e);
        } finally {
            releaseConnection(conn);
        }
        return list;
    }

    @Override
    public boolean hasBadge(Long patientId, String badgeType) {
        String sql = "SELECT 1 FROM badges WHERE patient_id = ? AND badge_type = ? LIMIT 1";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, patientId);
            stmt.setString(2, badgeType);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to check badge status", e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public List<Badge> findAll() {
        String sql = "SELECT * FROM badges ORDER BY id DESC";
        List<Badge> list = new ArrayList<>();
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch badges", e);
        } finally {
            releaseConnection(conn);
        }
        return list;
    }

    @Override
    public Badge save(Badge entity) {
        String sql = "INSERT INTO badges (patient_id, badge_type, title, description, icon_name, awarded_at) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, entity.getPatientId());
            stmt.setString(2, entity.getBadgeType());
            stmt.setString(3, entity.getTitle());
            stmt.setString(4, entity.getDescription());
            stmt.setString(5, entity.getIconName());
            stmt.setTimestamp(6, toTimestamp(entity.getAwardedAt()));

            stmt.executeUpdate();
            Long generatedId = getGeneratedId(stmt);
            if (generatedId != null) {
                entity.setId(generatedId);
            }
            return entity;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save badge", e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public void update(Badge entity) {
        // Badges are immutable awards
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM badges WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete badge: " + id, e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM badges";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count badges", e);
        } finally {
            releaseConnection(conn);
        }
        return 0L;
    }

    private Badge mapRow(ResultSet rs) throws SQLException {
        Badge b = new Badge();
        b.setId(rs.getLong("id"));
        b.setPatientId(rs.getLong("patient_id"));
        b.setBadgeType(rs.getString("badge_type"));
        b.setTitle(rs.getString("title"));
        b.setDescription(rs.getString("description"));
        b.setIconName(rs.getString("icon_name"));
        b.setAwardedAt(toLocalDateTime(rs.getTimestamp("awarded_at")));
        return b;
    }
}
