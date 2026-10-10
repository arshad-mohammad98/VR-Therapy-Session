package com.vrtherapy.dao.impl;

import com.vrtherapy.dao.FeedbackDao;
import com.vrtherapy.exception.DatabaseException;
import com.vrtherapy.model.Feedback;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FeedbackDaoImpl extends BaseJdbcDao implements FeedbackDao {

    @Override
    public Optional<Feedback> findById(Long id) {
        String sql = "SELECT * FROM feedbacks WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find feedback: " + id, e);
        } finally {
            releaseConnection(conn);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Feedback> findByResultId(Long resultId) {
        String sql = "SELECT * FROM feedbacks WHERE result_id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, resultId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find feedback by result ID: " + resultId, e);
        } finally {
            releaseConnection(conn);
        }
        return Optional.empty();
    }

    @Override
    public List<Feedback> findAll() {
        String sql = "SELECT * FROM feedbacks ORDER BY id DESC";
        List<Feedback> list = new ArrayList<>();
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch all feedback", e);
        } finally {
            releaseConnection(conn);
        }
        return list;
    }

    @Override
    public List<Feedback> findByPatientId(Long patientId) {
        String sql = "SELECT * FROM feedbacks WHERE patient_id = ? ORDER BY id DESC";
        List<Feedback> list = new ArrayList<>();
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, patientId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find feedback for patient: " + patientId, e);
        } finally {
            releaseConnection(conn);
        }
        return list;
    }

    @Override
    public List<Feedback> findBySessionId(Long sessionId) {
        String sql = "SELECT * FROM feedbacks WHERE session_id = ? ORDER BY id DESC";
        List<Feedback> list = new ArrayList<>();
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, sessionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find feedback for session: " + sessionId, e);
        } finally {
            releaseConnection(conn);
        }
        return list;
    }

    @Override
    public void updateAppliedAdjustment(Long feedbackId, String adjustment) {
        String sql = "UPDATE feedbacks SET applied_adjustment = ? WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, adjustment);
            stmt.setLong(2, feedbackId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update feedback adjustment: " + feedbackId, e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public Feedback save(Feedback entity) {
        String sql = "INSERT INTO feedbacks (result_id, patient_id, session_id, rating, comfort_score, nausea_score, comments, applied_adjustment) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (entity.getResultId() != null) stmt.setLong(1, entity.getResultId()); else stmt.setNull(1, java.sql.Types.BIGINT);
            stmt.setLong(2, entity.getPatientId());
            stmt.setLong(3, entity.getSessionId());
            stmt.setInt(4, entity.getRating());
            stmt.setInt(5, entity.getComfortScore());
            stmt.setInt(6, entity.getNauseaScore());
            stmt.setString(7, entity.getComments());
            stmt.setString(8, entity.getAppliedAdjustment());

            stmt.executeUpdate();
            Long generatedId = getGeneratedId(stmt);
            if (generatedId != null) {
                entity.setId(generatedId);
            }
            return entity;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save feedback", e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public void update(Feedback entity) {
        String sql = "UPDATE feedbacks SET rating = ?, comfort_score = ?, nausea_score = ?, comments = ?, applied_adjustment = ? WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, entity.getRating());
            stmt.setInt(2, entity.getComfortScore());
            stmt.setInt(3, entity.getNauseaScore());
            stmt.setString(4, entity.getComments());
            stmt.setString(5, entity.getAppliedAdjustment());
            stmt.setLong(6, entity.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update feedback: " + entity.getId(), e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM feedbacks WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete feedback: " + id, e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM feedbacks";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count feedback", e);
        } finally {
            releaseConnection(conn);
        }
        return 0L;
    }

    private Feedback mapRow(ResultSet rs) throws SQLException {
        Feedback fb = new Feedback();
        fb.setId(rs.getLong("id"));
        long resId = rs.getLong("result_id");
        if (!rs.wasNull()) {
            fb.setResultId(resId);
        }
        fb.setPatientId(rs.getLong("patient_id"));
        fb.setSessionId(rs.getLong("session_id"));
        fb.setRating(rs.getInt("rating"));
        fb.setComfortScore(rs.getInt("comfort_score"));
        fb.setNauseaScore(rs.getInt("nausea_score"));
        fb.setComments(rs.getString("comments"));
        fb.setAppliedAdjustment(rs.getString("applied_adjustment"));
        fb.setCreatedAt(toLocalDateTime(rs.getTimestamp("created_at")));
        return fb;
    }
}
