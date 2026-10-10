package com.vrtherapy.dao.impl;

import com.vrtherapy.dao.MoodDao;
import com.vrtherapy.exception.DatabaseException;
import com.vrtherapy.model.MoodEntry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MoodDaoImpl extends BaseJdbcDao implements MoodDao {

    @Override
    public Optional<MoodEntry> findById(Long id) {
        String sql = "SELECT * FROM mood_entries WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find mood entry: " + id, e);
        } finally {
            releaseConnection(conn);
        }
        return Optional.empty();
    }

    @Override
    public List<MoodEntry> findByPatientIdOrderByRecordedAtDesc(Long patientId) {
        String sql = "SELECT * FROM mood_entries WHERE patient_id = ? ORDER BY recorded_at DESC";
        List<MoodEntry> list = new ArrayList<>();
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, patientId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find mood entries for patient: " + patientId, e);
        } finally {
            releaseConnection(conn);
        }
        return list;
    }

    @Override
    public List<MoodEntry> findRecentByPatientId(Long patientId, int limit) {
        String sql = "SELECT * FROM mood_entries WHERE patient_id = ? ORDER BY recorded_at DESC LIMIT ?";
        List<MoodEntry> list = new ArrayList<>();
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, patientId);
            stmt.setInt(2, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find recent moods", e);
        } finally {
            releaseConnection(conn);
        }
        return list;
    }

    @Override
    public List<MoodEntry> findAll() {
        String sql = "SELECT * FROM mood_entries ORDER BY id DESC";
        List<MoodEntry> list = new ArrayList<>();
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch all mood entries", e);
        } finally {
            releaseConnection(conn);
        }
        return list;
    }

    @Override
    public MoodEntry save(MoodEntry entity) {
        String sql = "INSERT INTO mood_entries (patient_id, score, primary_emotion, notes, recorded_at) VALUES (?, ?, ?, ?, ?)";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, entity.getPatientId());
            stmt.setInt(2, entity.getScore());
            stmt.setString(3, entity.getPrimaryEmotion());
            stmt.setString(4, entity.getNotes());
            stmt.setTimestamp(5, toTimestamp(entity.getRecordedAt()));

            stmt.executeUpdate();
            Long generatedId = getGeneratedId(stmt);
            if (generatedId != null) {
                entity.setId(generatedId);
            }
            return entity;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save mood entry", e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public void update(MoodEntry entity) {
        String sql = "UPDATE mood_entries SET score = ?, primary_emotion = ?, notes = ? WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, entity.getScore());
            stmt.setString(2, entity.getPrimaryEmotion());
            stmt.setString(3, entity.getNotes());
            stmt.setLong(4, entity.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update mood entry: " + entity.getId(), e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM mood_entries WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete mood entry: " + id, e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM mood_entries";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count mood entries", e);
        } finally {
            releaseConnection(conn);
        }
        return 0L;
    }

    private MoodEntry mapRow(ResultSet rs) throws SQLException {
        MoodEntry me = new MoodEntry();
        me.setId(rs.getLong("id"));
        me.setPatientId(rs.getLong("patient_id"));
        me.setScore(rs.getInt("score"));
        me.setPrimaryEmotion(rs.getString("primary_emotion"));
        me.setNotes(rs.getString("notes"));
        me.setRecordedAt(toLocalDateTime(rs.getTimestamp("recorded_at")));
        return me;
    }
}
