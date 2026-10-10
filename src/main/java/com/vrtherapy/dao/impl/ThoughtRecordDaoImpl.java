package com.vrtherapy.dao.impl;

import com.vrtherapy.dao.ThoughtRecordDao;
import com.vrtherapy.exception.DatabaseException;
import com.vrtherapy.model.ThoughtRecord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ThoughtRecordDaoImpl extends BaseJdbcDao implements ThoughtRecordDao {

    @Override
    public Optional<ThoughtRecord> findById(Long id) {
        String sql = "SELECT * FROM thought_records WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find thought record: " + id, e);
        } finally {
            releaseConnection(conn);
        }
        return Optional.empty();
    }

    @Override
    public List<ThoughtRecord> findByPatientId(Long patientId) {
        String sql = "SELECT * FROM thought_records WHERE patient_id = ? ORDER BY id DESC";
        List<ThoughtRecord> list = new ArrayList<>();
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, patientId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find thought records for patient: " + patientId, e);
        } finally {
            releaseConnection(conn);
        }
        return list;
    }

    @Override
    public List<ThoughtRecord> findAll() {
        String sql = "SELECT * FROM thought_records ORDER BY id DESC";
        List<ThoughtRecord> list = new ArrayList<>();
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch thought records", e);
        } finally {
            releaseConnection(conn);
        }
        return list;
    }

    @Override
    public ThoughtRecord save(ThoughtRecord entity) {
        String sql = "INSERT INTO thought_records (patient_id, situation, automatic_thought, cognitive_distortion, rational_response, outcome_belief) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, entity.getPatientId());
            stmt.setString(2, entity.getSituation());
            stmt.setString(3, entity.getAutomaticThought());
            stmt.setString(4, entity.getCognitiveDistortion());
            stmt.setString(5, entity.getRationalResponse());
            stmt.setInt(6, entity.getOutcomeBelief());

            stmt.executeUpdate();
            Long generatedId = getGeneratedId(stmt);
            if (generatedId != null) {
                entity.setId(generatedId);
            }
            return entity;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save thought record", e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public void update(ThoughtRecord entity) {
        String sql = "UPDATE thought_records SET situation = ?, automatic_thought = ?, cognitive_distortion = ?, " +
                "rational_response = ?, outcome_belief = ? WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entity.getSituation());
            stmt.setString(2, entity.getAutomaticThought());
            stmt.setString(3, entity.getCognitiveDistortion());
            stmt.setString(4, entity.getRationalResponse());
            stmt.setInt(5, entity.getOutcomeBelief());
            stmt.setLong(6, entity.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update thought record: " + entity.getId(), e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM thought_records WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete thought record: " + id, e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM thought_records";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count thought records", e);
        } finally {
            releaseConnection(conn);
        }
        return 0L;
    }

    private ThoughtRecord mapRow(ResultSet rs) throws SQLException {
        ThoughtRecord tr = new ThoughtRecord();
        tr.setId(rs.getLong("id"));
        tr.setPatientId(rs.getLong("patient_id"));
        tr.setSituation(rs.getString("situation"));
        tr.setAutomaticThought(rs.getString("automatic_thought"));
        tr.setCognitiveDistortion(rs.getString("cognitive_distortion"));
        tr.setRationalResponse(rs.getString("rational_response"));
        tr.setOutcomeBelief(rs.getInt("outcome_belief"));
        tr.setCreatedAt(toLocalDateTime(rs.getTimestamp("created_at")));
        return tr;
    }
}
