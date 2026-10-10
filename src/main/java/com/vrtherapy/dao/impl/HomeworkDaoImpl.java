package com.vrtherapy.dao.impl;

import com.vrtherapy.dao.HomeworkDao;
import com.vrtherapy.exception.DatabaseException;
import com.vrtherapy.model.Homework;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HomeworkDaoImpl extends BaseJdbcDao implements HomeworkDao {

    @Override
    public Optional<Homework> findById(Long id) {
        String sql = "SELECT * FROM homework WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find homework: " + id, e);
        } finally {
            releaseConnection(conn);
        }
        return Optional.empty();
    }

    @Override
    public List<Homework> findByPatientId(Long patientId) {
        String sql = "SELECT * FROM homework WHERE patient_id = ? ORDER BY due_date ASC";
        List<Homework> list = new ArrayList<>();
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, patientId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find homework for patient: " + patientId, e);
        } finally {
            releaseConnection(conn);
        }
        return list;
    }

    @Override
    public List<Homework> findByTherapistId(Long therapistId) {
        String sql = "SELECT * FROM homework WHERE therapist_id = ? ORDER BY due_date ASC";
        List<Homework> list = new ArrayList<>();
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, therapistId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find homework for therapist: " + therapistId, e);
        } finally {
            releaseConnection(conn);
        }
        return list;
    }

    @Override
    public void setCompleted(Long homeworkId, boolean completed) {
        String sql = "UPDATE homework SET completed = ? WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, completed);
            stmt.setLong(2, homeworkId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update homework completion", e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public List<Homework> findPendingReminders() {
        String sql = "SELECT * FROM homework WHERE completed = FALSE AND reminder_sent = FALSE AND due_date <= CURRENT_DATE + 2";
        List<Homework> list = new ArrayList<>();
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find pending homework reminders", e);
        } finally {
            releaseConnection(conn);
        }
        return list;
    }

    @Override
    public void markReminderSent(Long homeworkId) {
        String sql = "UPDATE homework SET reminder_sent = TRUE WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, homeworkId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to mark reminder sent", e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public List<Homework> findAll() {
        String sql = "SELECT * FROM homework ORDER BY id DESC";
        List<Homework> list = new ArrayList<>();
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch all homework", e);
        } finally {
            releaseConnection(conn);
        }
        return list;
    }

    @Override
    public Homework save(Homework entity) {
        String sql = "INSERT INTO homework (patient_id, therapist_id, title, description, due_date, completed, reminder_sent) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, entity.getPatientId());
            stmt.setLong(2, entity.getTherapistId());
            stmt.setString(3, entity.getTitle());
            stmt.setString(4, entity.getDescription());
            stmt.setDate(5, toSqlDate(entity.getDueDate()));
            stmt.setBoolean(6, entity.isCompleted());
            stmt.setBoolean(7, entity.isReminderSent());

            stmt.executeUpdate();
            Long generatedId = getGeneratedId(stmt);
            if (generatedId != null) {
                entity.setId(generatedId);
            }
            return entity;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save homework", e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public void update(Homework entity) {
        String sql = "UPDATE homework SET title = ?, description = ?, due_date = ?, completed = ?, reminder_sent = ? WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entity.getTitle());
            stmt.setString(2, entity.getDescription());
            stmt.setDate(3, toSqlDate(entity.getDueDate()));
            stmt.setBoolean(4, entity.isCompleted());
            stmt.setBoolean(5, entity.isReminderSent());
            stmt.setLong(6, entity.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update homework: " + entity.getId(), e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM homework WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete homework: " + id, e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM homework";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count homework", e);
        } finally {
            releaseConnection(conn);
        }
        return 0L;
    }

    private Homework mapRow(ResultSet rs) throws SQLException {
        Homework hw = new Homework();
        hw.setId(rs.getLong("id"));
        hw.setPatientId(rs.getLong("patient_id"));
        hw.setTherapistId(rs.getLong("therapist_id"));
        hw.setTitle(rs.getString("title"));
        hw.setDescription(rs.getString("description"));
        hw.setDueDate(toLocalDate(rs.getDate("due_date")));
        hw.setCompleted(rs.getBoolean("completed"));
        hw.setReminderSent(rs.getBoolean("reminder_sent"));
        hw.setCreatedAt(toLocalDateTime(rs.getTimestamp("created_at")));
        return hw;
    }
}
