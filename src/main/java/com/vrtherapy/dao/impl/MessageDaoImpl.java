package com.vrtherapy.dao.impl;

import com.vrtherapy.dao.MessageDao;
import com.vrtherapy.exception.DatabaseException;
import com.vrtherapy.model.Message;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MessageDaoImpl extends BaseJdbcDao implements MessageDao {

    @Override
    public Optional<Message> findById(Long id) {
        String sql = "SELECT * FROM messages WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find message: " + id, e);
        } finally {
            releaseConnection(conn);
        }
        return Optional.empty();
    }

    @Override
    public List<Message> findConversation(Long userOneId, Long userTwoId) {
        String sql = "SELECT * FROM messages WHERE (sender_id = ? AND recipient_id = ?) OR (sender_id = ? AND recipient_id = ?) ORDER BY sent_at ASC";
        List<Message> list = new ArrayList<>();
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userOneId);
            stmt.setLong(2, userTwoId);
            stmt.setLong(3, userTwoId);
            stmt.setLong(4, userOneId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to load conversation", e);
        } finally {
            releaseConnection(conn);
        }
        return list;
    }

    @Override
    public long countUnreadMessages(Long recipientId) {
        String sql = "SELECT COUNT(*) FROM messages WHERE recipient_id = ? AND is_read = FALSE";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, recipientId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count unread messages", e);
        } finally {
            releaseConnection(conn);
        }
        return 0L;
    }

    @Override
    public void markConversationAsRead(Long senderId, Long recipientId) {
        String sql = "UPDATE messages SET is_read = TRUE WHERE sender_id = ? AND recipient_id = ? AND is_read = FALSE";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, senderId);
            stmt.setLong(2, recipientId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to mark conversation as read", e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public List<Message> findAll() {
        String sql = "SELECT * FROM messages ORDER BY sent_at DESC";
        List<Message> list = new ArrayList<>();
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch messages", e);
        } finally {
            releaseConnection(conn);
        }
        return list;
    }

    @Override
    public Message save(Message entity) {
        String sql = "INSERT INTO messages (sender_id, recipient_id, content, is_read, sent_at) VALUES (?, ?, ?, ?, ?)";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, entity.getSenderId());
            stmt.setLong(2, entity.getRecipientId());
            stmt.setString(3, entity.getContent());
            stmt.setBoolean(4, entity.isRead());
            stmt.setTimestamp(5, toTimestamp(entity.getSentAt()));

            stmt.executeUpdate();
            Long generatedId = getGeneratedId(stmt);
            if (generatedId != null) {
                entity.setId(generatedId);
            }
            return entity;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save message", e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public void update(Message entity) {
        String sql = "UPDATE messages SET is_read = ? WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, entity.isRead());
            stmt.setLong(2, entity.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update message: " + entity.getId(), e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM messages WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete message: " + id, e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM messages";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count messages", e);
        } finally {
            releaseConnection(conn);
        }
        return 0L;
    }

    private Message mapRow(ResultSet rs) throws SQLException {
        Message m = new Message();
        m.setId(rs.getLong("id"));
        m.setSenderId(rs.getLong("sender_id"));
        m.setRecipientId(rs.getLong("recipient_id"));
        m.setContent(rs.getString("content"));
        m.setRead(rs.getBoolean("is_read"));
        m.setSentAt(toLocalDateTime(rs.getTimestamp("sent_at")));
        return m;
    }
}
