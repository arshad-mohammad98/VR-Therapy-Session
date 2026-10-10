package com.vrtherapy.dao.impl;

import com.vrtherapy.dao.DeviceDao;
import com.vrtherapy.exception.DatabaseException;
import com.vrtherapy.model.DeviceStatus;
import com.vrtherapy.model.DeviceType;
import com.vrtherapy.model.VrDevice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DeviceDaoImpl extends BaseJdbcDao implements DeviceDao {

    @Override
    public Optional<VrDevice> findById(Long id) {
        String sql = "SELECT * FROM vr_devices WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find device by ID: " + id, e);
        } finally {
            releaseConnection(conn);
        }
        return Optional.empty();
    }

    @Override
    public Optional<VrDevice> findByPairingCode(String pairingCode) {
        String sql = "SELECT * FROM vr_devices WHERE pairing_code = ? AND is_blocked = FALSE";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, pairingCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find device by pairing code", e);
        } finally {
            releaseConnection(conn);
        }
        return Optional.empty();
    }

    @Override
    public List<VrDevice> findByPatientId(Long patientId) {
        String sql = "SELECT * FROM vr_devices WHERE patient_id = ? ORDER BY id DESC";
        List<VrDevice> list = new ArrayList<>();
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, patientId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find devices for patient: " + patientId, e);
        } finally {
            releaseConnection(conn);
        }
        return list;
    }

    @Override
    public void updateStatus(Long deviceId, DeviceStatus status) {
        String sql = "UPDATE vr_devices SET status = ? WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setLong(2, deviceId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update device status: " + deviceId, e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public void updateHeartbeat(Long deviceId, LocalDateTime heartbeat) {
        String sql = "UPDATE vr_devices SET last_heartbeat = ?, status = 'CONNECTED' WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, toTimestamp(heartbeat));
            stmt.setLong(2, deviceId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update heartbeat: " + deviceId, e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public void setBlocked(Long deviceId, boolean blocked) {
        String sql = "UPDATE vr_devices SET is_blocked = ?, status = CASE WHEN ? THEN 'DISCONNECTED' ELSE status END WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, blocked);
            stmt.setBoolean(2, blocked);
            stmt.setLong(3, deviceId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update device blocked state: " + deviceId, e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public List<VrDevice> findTimedOutDevices(LocalDateTime threshold) {
        String sql = "SELECT * FROM vr_devices WHERE status = 'CONNECTED' AND (last_heartbeat < ? OR last_heartbeat IS NULL)";
        List<VrDevice> list = new ArrayList<>();
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, toTimestamp(threshold));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find timed out devices", e);
        } finally {
            releaseConnection(conn);
        }
        return list;
    }

    @Override
    public List<VrDevice> findAll() {
        String sql = "SELECT * FROM vr_devices ORDER BY id DESC";
        List<VrDevice> list = new ArrayList<>();
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find all devices", e);
        } finally {
            releaseConnection(conn);
        }
        return list;
    }

    @Override
    public VrDevice save(VrDevice entity) {
        String sql = "INSERT INTO vr_devices (device_name, device_type, pairing_code, code_expires_at, status, last_heartbeat, patient_id, ip_address, is_blocked) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, entity.getDeviceName());
            stmt.setString(2, entity.getDeviceType().name());
            stmt.setString(3, entity.getPairingCode());
            stmt.setTimestamp(4, toTimestamp(entity.getCodeExpiresAt()));
            stmt.setString(5, entity.getStatus().name());
            stmt.setTimestamp(6, toTimestamp(entity.getLastHeartbeat()));
            if (entity.getPatientId() != null) stmt.setLong(7, entity.getPatientId()); else stmt.setNull(7, java.sql.Types.BIGINT);
            stmt.setString(8, entity.getIpAddress());
            stmt.setBoolean(9, entity.isBlocked());

            stmt.executeUpdate();
            Long generatedId = getGeneratedId(stmt);
            if (generatedId != null) {
                entity.setId(generatedId);
            }
            return entity;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save device", e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public void update(VrDevice entity) {
        String sql = "UPDATE vr_devices SET device_name = ?, device_type = ?, pairing_code = ?, code_expires_at = ?, " +
                "status = ?, last_heartbeat = ?, patient_id = ?, ip_address = ?, is_blocked = ? WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entity.getDeviceName());
            stmt.setString(2, entity.getDeviceType().name());
            stmt.setString(3, entity.getPairingCode());
            stmt.setTimestamp(4, toTimestamp(entity.getCodeExpiresAt()));
            stmt.setString(5, entity.getStatus().name());
            stmt.setTimestamp(6, toTimestamp(entity.getLastHeartbeat()));
            if (entity.getPatientId() != null) stmt.setLong(7, entity.getPatientId()); else stmt.setNull(7, java.sql.Types.BIGINT);
            stmt.setString(8, entity.getIpAddress());
            stmt.setBoolean(9, entity.isBlocked());
            stmt.setLong(10, entity.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update device: " + entity.getId(), e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM vr_devices WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete device: " + id, e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM vr_devices";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count devices", e);
        } finally {
            releaseConnection(conn);
        }
        return 0L;
    }

    private VrDevice mapRow(ResultSet rs) throws SQLException {
        VrDevice dev = new VrDevice();
        dev.setId(rs.getLong("id"));
        dev.setDeviceName(rs.getString("device_name"));
        String typeStr = rs.getString("device_type");
        try {
            dev.setDeviceType(DeviceType.valueOf(typeStr));
        } catch (Exception ignored) {
            dev.setDeviceType(DeviceType.HEADSET);
        }
        dev.setPairingCode(rs.getString("pairing_code"));
        dev.setCodeExpiresAt(toLocalDateTime(rs.getTimestamp("code_expires_at")));
        String statusStr = rs.getString("status");
        try {
            dev.setStatus(DeviceStatus.valueOf(statusStr));
        } catch (Exception ignored) {
            dev.setStatus(DeviceStatus.DISCONNECTED);
        }
        dev.setLastHeartbeat(toLocalDateTime(rs.getTimestamp("last_heartbeat")));
        long patId = rs.getLong("patient_id");
        if (!rs.wasNull()) {
            dev.setPatientId(patId);
        }
        dev.setIpAddress(rs.getString("ip_address"));
        dev.setBlocked(rs.getBoolean("is_blocked"));
        dev.setCreatedAt(toLocalDateTime(rs.getTimestamp("created_at")));
        return dev;
    }
}
