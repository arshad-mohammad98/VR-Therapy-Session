package com.vrtherapy.dao.impl;

import com.vrtherapy.dao.PlanDao;
import com.vrtherapy.exception.DatabaseException;
import com.vrtherapy.model.ConditionType;
import com.vrtherapy.model.TherapyPlan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlanDaoImpl extends BaseJdbcDao implements PlanDao {

    @Override
    public Optional<TherapyPlan> findById(Long id) {
        String sql = "SELECT * FROM therapy_plans WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find plan by ID: " + id, e);
        } finally {
            releaseConnection(conn);
        }
        return Optional.empty();
    }

    @Override
    public List<TherapyPlan> findAll() {
        String sql = "SELECT * FROM therapy_plans ORDER BY id DESC";
        List<TherapyPlan> list = new ArrayList<>();
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find all plans", e);
        } finally {
            releaseConnection(conn);
        }
        return list;
    }

    @Override
    public List<TherapyPlan> findByPatientId(Long patientId) {
        String sql = "SELECT * FROM therapy_plans WHERE patient_id = ? ORDER BY id DESC";
        List<TherapyPlan> list = new ArrayList<>();
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, patientId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find plans for patient: " + patientId, e);
        } finally {
            releaseConnection(conn);
        }
        return list;
    }

    @Override
    public List<TherapyPlan> findByTherapistId(Long therapistId) {
        String sql = "SELECT * FROM therapy_plans WHERE therapist_id = ? ORDER BY id DESC";
        List<TherapyPlan> list = new ArrayList<>();
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, therapistId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find plans for therapist: " + therapistId, e);
        } finally {
            releaseConnection(conn);
        }
        return list;
    }

    @Override
    public TherapyPlan save(TherapyPlan entity) {
        String sql = "INSERT INTO therapy_plans (patient_id, therapist_id, title, condition_type, total_weeks, status, goals) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, entity.getPatientId());
            stmt.setLong(2, entity.getTherapistId());
            stmt.setString(3, entity.getTitle());
            stmt.setString(4, entity.getConditionType().name());
            stmt.setInt(5, entity.getTotalWeeks());
            stmt.setString(6, entity.getStatus());
            stmt.setString(7, entity.getGoals());

            stmt.executeUpdate();
            Long generatedId = getGeneratedId(stmt);
            if (generatedId != null) {
                entity.setId(generatedId);
            }
            return entity;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save plan", e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public void update(TherapyPlan entity) {
        String sql = "UPDATE therapy_plans SET title = ?, condition_type = ?, total_weeks = ?, status = ?, goals = ?, " +
                "updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entity.getTitle());
            stmt.setString(2, entity.getConditionType().name());
            stmt.setInt(3, entity.getTotalWeeks());
            stmt.setString(4, entity.getStatus());
            stmt.setString(5, entity.getGoals());
            stmt.setLong(6, entity.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update plan: " + entity.getId(), e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM therapy_plans WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete plan: " + id, e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM therapy_plans";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count plans", e);
        } finally {
            releaseConnection(conn);
        }
        return 0L;
    }

    private TherapyPlan mapRow(ResultSet rs) throws SQLException {
        TherapyPlan plan = new TherapyPlan();
        plan.setId(rs.getLong("id"));
        plan.setPatientId(rs.getLong("patient_id"));
        plan.setTherapistId(rs.getLong("therapist_id"));
        plan.setTitle(rs.getString("title"));

        String cond = rs.getString("condition_type");
        try {
            plan.setConditionType(ConditionType.valueOf(cond.toUpperCase().replace(" ", "_")));
        } catch (Exception ignored) {
            plan.setConditionType(ConditionType.ANXIETY);
        }

        plan.setTotalWeeks(rs.getInt("total_weeks"));
        plan.setStatus(rs.getString("status"));
        plan.setGoals(rs.getString("goals"));
        plan.setCreatedAt(toLocalDateTime(rs.getTimestamp("created_at")));
        plan.setUpdatedAt(toLocalDateTime(rs.getTimestamp("updated_at")));
        return plan;
    }
}
