package com.vrtherapy.dao.impl;

import com.vrtherapy.dao.SettingDao;
import com.vrtherapy.exception.DatabaseException;
import com.vrtherapy.model.SystemSetting;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SettingDaoImpl extends BaseJdbcDao implements SettingDao {

    @Override
    public Optional<SystemSetting> findById(Long id) {
        String sql = "SELECT * FROM system_settings WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find setting by ID: " + id, e);
        } finally {
            releaseConnection(conn);
        }
        return Optional.empty();
    }

    @Override
    public Optional<SystemSetting> findByKey(String settingKey) {
        String sql = "SELECT * FROM system_settings WHERE setting_key = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, settingKey);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find setting by key: " + settingKey, e);
        } finally {
            releaseConnection(conn);
        }
        return Optional.empty();
    }

    @Override
    public void updateValue(String settingKey, String settingValue) {
        String sql = "UPDATE system_settings SET setting_value = ?, updated_at = CURRENT_TIMESTAMP WHERE setting_key = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, settingValue);
            stmt.setString(2, settingKey);
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                // Insert if not existing
                String insertSql = "INSERT INTO system_settings (setting_key, setting_value, description) VALUES (?, ?, ?)";
                try (PreparedStatement ins = conn.prepareStatement(insertSql)) {
                    ins.setString(1, settingKey);
                    ins.setString(2, settingValue);
                    ins.setString(3, "System configuration parameter");
                    ins.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update setting: " + settingKey, e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public String getSettingValue(String key, String defaultValue) {
        return findByKey(key).map(SystemSetting::getSettingValue).orElse(defaultValue);
    }

    @Override
    public List<SystemSetting> findAll() {
        String sql = "SELECT * FROM system_settings ORDER BY setting_key ASC";
        List<SystemSetting> list = new ArrayList<>();
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch settings", e);
        } finally {
            releaseConnection(conn);
        }
        return list;
    }

    @Override
    public SystemSetting save(SystemSetting entity) {
        String sql = "INSERT INTO system_settings (setting_key, setting_value, description) VALUES (?, ?, ?)";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, entity.getSettingKey());
            stmt.setString(2, entity.getSettingValue());
            stmt.setString(3, entity.getDescription());
            stmt.executeUpdate();
            Long genId = getGeneratedId(stmt);
            if (genId != null) {
                entity.setId(genId);
            }
            return entity;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save setting", e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public void update(SystemSetting entity) {
        String sql = "UPDATE system_settings SET setting_value = ?, description = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entity.getSettingValue());
            stmt.setString(2, entity.getDescription());
            stmt.setLong(3, entity.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update setting: " + entity.getId(), e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM system_settings WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete setting: " + id, e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM system_settings";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count settings", e);
        } finally {
            releaseConnection(conn);
        }
        return 0L;
    }

    private SystemSetting mapRow(ResultSet rs) throws SQLException {
        SystemSetting s = new SystemSetting();
        s.setId(rs.getLong("id"));
        s.setSettingKey(rs.getString("setting_key"));
        s.setSettingValue(rs.getString("setting_value"));
        s.setDescription(rs.getString("description"));
        s.setUpdatedAt(toLocalDateTime(rs.getTimestamp("updated_at")));
        return s;
    }
}
