package com.vrtherapy.dao.impl;

import com.vrtherapy.dao.UserDao;
import com.vrtherapy.exception.DatabaseException;
import com.vrtherapy.model.Admin;
import com.vrtherapy.model.Patient;
import com.vrtherapy.model.Role;
import com.vrtherapy.model.Therapist;
import com.vrtherapy.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Raw JDBC implementation of UserDao supporting polymorphic User instantiation.
 *
 * Demonstrates:
 * - Raw JDBC PreparedStatement with parameterized queries
 * - Polymorphic row mapping into Patient, Therapist, or Admin
 * - Safe resource cleanup with try-with-resources
 */
public class UserDaoImpl extends BaseJdbcDao implements UserDao {

    @Override
    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToUser(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find user by ID: " + id, e);
        } finally {
            releaseConnection(conn);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToUser(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find user by username: " + username, e);
        } finally {
            releaseConnection(conn);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToUser(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find user by email: " + email, e);
        } finally {
            releaseConnection(conn);
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM users ORDER BY id ASC";
        List<User> list = new ArrayList<>();
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapRowToUser(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch all users", e);
        } finally {
            releaseConnection(conn);
        }
        return list;
    }

    @Override
    public List<User> findByRole(Role role) {
        String sql = "SELECT * FROM users WHERE role = ? AND active = TRUE ORDER BY full_name ASC";
        List<User> list = new ArrayList<>();
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, role.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToUser(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find users by role: " + role, e);
        } finally {
            releaseConnection(conn);
        }
        return list;
    }

    @Override
    public List<Patient> findPatientsByTherapistId(Long therapistId) {
        String sql = "SELECT * FROM users WHERE role = 'PATIENT' AND (assigned_therapist_id = ? OR assigned_therapist_id IS NULL) ORDER BY full_name ASC";
        List<Patient> list = new ArrayList<>();
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, therapistId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    User u = mapRowToUser(rs);
                    if (u instanceof Patient p) {
                        list.add(p);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find patients for therapist: " + therapistId, e);
        } finally {
            releaseConnection(conn);
        }
        return list;
    }

    @Override
    public List<Therapist> findAllTherapists() {
        List<User> users = findByRole(Role.THERAPIST);
        List<Therapist> therapists = new ArrayList<>();
        for (User u : users) {
            if (u instanceof Therapist t) {
                therapists.add(t);
            }
        }
        return therapists;
    }

    @Override
    public boolean existsByUsername(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ? LIMIT 1";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to check username existence", e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        String sql = "SELECT 1 FROM users WHERE email = ? LIMIT 1";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to check email existence", e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public long countByRole(Role role) {
        String sql = "SELECT COUNT(*) FROM users WHERE role = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, role.name());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count users by role", e);
        } finally {
            releaseConnection(conn);
        }
        return 0L;
    }

    @Override
    public long countActiveUsers() {
        String sql = "SELECT COUNT(*) FROM users WHERE active = TRUE";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count active users", e);
        } finally {
            releaseConnection(conn);
        }
        return 0L;
    }

    @Override
    public User save(User entity) {
        String sql = "INSERT INTO users (username, password, full_name, email, role, active, phone, clinical_bio_or_notes, " +
                "assigned_therapist_id, emergency_contact, baseline_anxiety, license_number, specialization, department) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, entity.getUsername());
            stmt.setString(2, entity.getPassword());
            stmt.setString(3, entity.getFullName());
            stmt.setString(4, entity.getEmail());
            stmt.setString(5, entity.getRole().name());
            stmt.setBoolean(6, entity.isActive());
            stmt.setString(7, entity.getPhone());
            stmt.setString(8, entity.getClinicalBioOrNotes());

            if (entity instanceof Patient p) {
                if (p.getAssignedTherapistId() != null) stmt.setLong(9, p.getAssignedTherapistId()); else stmt.setNull(9, java.sql.Types.BIGINT);
                stmt.setString(10, p.getEmergencyContact());
                stmt.setInt(11, p.getBaselineAnxiety() != null ? p.getBaselineAnxiety() : 5);
                stmt.setNull(12, java.sql.Types.VARCHAR);
                stmt.setNull(13, java.sql.Types.VARCHAR);
                stmt.setNull(14, java.sql.Types.VARCHAR);
            } else if (entity instanceof Therapist t) {
                stmt.setNull(9, java.sql.Types.BIGINT);
                stmt.setNull(10, java.sql.Types.VARCHAR);
                stmt.setNull(11, java.sql.Types.INTEGER);
                stmt.setString(12, t.getLicenseNumber());
                stmt.setString(13, t.getSpecialization());
                stmt.setNull(14, java.sql.Types.VARCHAR);
            } else if (entity instanceof Admin a) {
                stmt.setNull(9, java.sql.Types.BIGINT);
                stmt.setNull(10, java.sql.Types.VARCHAR);
                stmt.setNull(11, java.sql.Types.INTEGER);
                stmt.setNull(12, java.sql.Types.VARCHAR);
                stmt.setNull(13, java.sql.Types.VARCHAR);
                stmt.setString(14, a.getDepartment());
            } else {
                stmt.setNull(9, java.sql.Types.BIGINT);
                stmt.setNull(10, java.sql.Types.VARCHAR);
                stmt.setNull(11, java.sql.Types.INTEGER);
                stmt.setNull(12, java.sql.Types.VARCHAR);
                stmt.setNull(13, java.sql.Types.VARCHAR);
                stmt.setNull(14, java.sql.Types.VARCHAR);
            }

            stmt.executeUpdate();
            Long generatedId = getGeneratedId(stmt);
            if (generatedId != null) {
                entity.setId(generatedId);
            }
            return entity;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save user: " + entity.getUsername(), e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public void update(User entity) {
        String sql = "UPDATE users SET full_name = ?, email = ?, active = ?, phone = ?, clinical_bio_or_notes = ?, " +
                "assigned_therapist_id = ?, emergency_contact = ?, baseline_anxiety = ?, license_number = ?, specialization = ?, " +
                "department = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entity.getFullName());
            stmt.setString(2, entity.getEmail());
            stmt.setBoolean(3, entity.isActive());
            stmt.setString(4, entity.getPhone());
            stmt.setString(5, entity.getClinicalBioOrNotes());

            if (entity instanceof Patient p) {
                if (p.getAssignedTherapistId() != null) stmt.setLong(6, p.getAssignedTherapistId()); else stmt.setNull(6, java.sql.Types.BIGINT);
                stmt.setString(7, p.getEmergencyContact());
                stmt.setInt(8, p.getBaselineAnxiety() != null ? p.getBaselineAnxiety() : 5);
                stmt.setNull(9, java.sql.Types.VARCHAR);
                stmt.setNull(10, java.sql.Types.VARCHAR);
                stmt.setNull(11, java.sql.Types.VARCHAR);
            } else if (entity instanceof Therapist t) {
                stmt.setNull(6, java.sql.Types.BIGINT);
                stmt.setNull(7, java.sql.Types.VARCHAR);
                stmt.setNull(8, java.sql.Types.INTEGER);
                stmt.setString(9, t.getLicenseNumber());
                stmt.setString(10, t.getSpecialization());
                stmt.setNull(11, java.sql.Types.VARCHAR);
            } else if (entity instanceof Admin a) {
                stmt.setNull(6, java.sql.Types.BIGINT);
                stmt.setNull(7, java.sql.Types.VARCHAR);
                stmt.setNull(8, java.sql.Types.INTEGER);
                stmt.setNull(9, java.sql.Types.VARCHAR);
                stmt.setNull(10, java.sql.Types.VARCHAR);
                stmt.setString(11, a.getDepartment());
            } else {
                stmt.setNull(6, java.sql.Types.BIGINT);
                stmt.setNull(7, java.sql.Types.VARCHAR);
                stmt.setNull(8, java.sql.Types.INTEGER);
                stmt.setNull(9, java.sql.Types.VARCHAR);
                stmt.setNull(10, java.sql.Types.VARCHAR);
                stmt.setNull(11, java.sql.Types.VARCHAR);
            }
            stmt.setLong(12, entity.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update user: " + entity.getId(), e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM users WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete user: " + id, e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM users";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count users", e);
        } finally {
            releaseConnection(conn);
        }
        return 0L;
    }

    private User mapRowToUser(ResultSet rs) throws SQLException {
        String roleStr = rs.getString("role");
        Role role = Role.valueOf(roleStr);

        User user;
        if (role == Role.PATIENT) {
            Patient p = new Patient();
            long assignedTherapist = rs.getLong("assigned_therapist_id");
            if (!rs.wasNull()) {
                p.setAssignedTherapistId(assignedTherapist);
            }
            p.setEmergencyContact(rs.getString("emergency_contact"));
            int baseline = rs.getInt("baseline_anxiety");
            p.setBaselineAnxiety(rs.wasNull() ? 5 : baseline);
            user = p;
        } else if (role == Role.THERAPIST) {
            Therapist t = new Therapist();
            t.setLicenseNumber(rs.getString("license_number"));
            t.setSpecialization(rs.getString("specialization"));
            user = t;
        } else {
            Admin a = new Admin();
            a.setDepartment(rs.getString("department"));
            user = a;
        }

        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setRole(role);
        user.setActive(rs.getBoolean("active"));
        user.setPhone(rs.getString("phone"));
        user.setClinicalBioOrNotes(rs.getString("clinical_bio_or_notes"));
        user.setCreatedAt(toLocalDateTime(rs.getTimestamp("created_at")));
        user.setUpdatedAt(toLocalDateTime(rs.getTimestamp("updated_at")));

        return user;
    }
}
