package com.vrtherapy.dao.impl;

import com.vrtherapy.dao.TicketDao;
import com.vrtherapy.exception.DatabaseException;
import com.vrtherapy.model.SupportTicket;
import com.vrtherapy.model.TicketPriority;
import com.vrtherapy.model.TicketReply;
import com.vrtherapy.model.TicketStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TicketDaoImpl extends BaseJdbcDao implements TicketDao {

    @Override
    public Optional<SupportTicket> findById(Long id) {
        String sql = "SELECT * FROM support_tickets WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    SupportTicket ticket = mapRow(rs);
                    ticket.setReplies(findRepliesByTicketId(id));
                    return Optional.of(ticket);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find ticket by ID: " + id, e);
        } finally {
            releaseConnection(conn);
        }
        return Optional.empty();
    }

    @Override
    public List<SupportTicket> findByCreatedBy(Long createdBy) {
        String sql = "SELECT * FROM support_tickets WHERE created_by = ? ORDER BY id DESC";
        List<SupportTicket> list = new ArrayList<>();
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, createdBy);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find tickets by creator: " + createdBy, e);
        } finally {
            releaseConnection(conn);
        }
        return list;
    }

    @Override
    public List<SupportTicket> findByStatus(TicketStatus status) {
        String sql = "SELECT * FROM support_tickets WHERE status = ? ORDER BY id DESC";
        List<SupportTicket> list = new ArrayList<>();
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find tickets by status: " + status, e);
        } finally {
            releaseConnection(conn);
        }
        return list;
    }

    @Override
    public void updateStatus(Long ticketId, TicketStatus status) {
        String sql = "UPDATE support_tickets SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setLong(2, ticketId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update ticket status: " + ticketId, e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public void addReply(TicketReply reply) {
        String sql = "INSERT INTO ticket_replies (ticket_id, author_id, message) VALUES (?, ?, ?)";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, reply.getTicketId());
            stmt.setLong(2, reply.getAuthorId());
            stmt.setString(3, reply.getMessage());
            stmt.executeUpdate();
            Long genId = getGeneratedId(stmt);
            if (genId != null) {
                reply.setId(genId);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to add ticket reply", e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public List<TicketReply> findRepliesByTicketId(Long ticketId) {
        String sql = "SELECT tr.*, u.full_name as author_name FROM ticket_replies tr " +
                "JOIN users u ON tr.author_id = u.id WHERE tr.ticket_id = ? ORDER BY tr.created_at ASC";
        List<TicketReply> list = new ArrayList<>();
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, ticketId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    TicketReply r = new TicketReply();
                    r.setId(rs.getLong("id"));
                    r.setTicketId(rs.getLong("ticket_id"));
                    r.setAuthorId(rs.getLong("author_id"));
                    r.setAuthorName(rs.getString("author_name"));
                    r.setMessage(rs.getString("message"));
                    r.setCreatedAt(toLocalDateTime(rs.getTimestamp("created_at")));
                    list.add(r);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to load replies for ticket: " + ticketId, e);
        } finally {
            releaseConnection(conn);
        }
        return list;
    }

    @Override
    public List<SupportTicket> findAll() {
        String sql = "SELECT * FROM support_tickets ORDER BY id DESC";
        List<SupportTicket> list = new ArrayList<>();
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch all tickets", e);
        } finally {
            releaseConnection(conn);
        }
        return list;
    }

    @Override
    public SupportTicket save(SupportTicket entity) {
        String sql = "INSERT INTO support_tickets (created_by, title, description, priority, status) VALUES (?, ?, ?, ?, ?)";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, entity.getCreatedBy());
            stmt.setString(2, entity.getTitle());
            stmt.setString(3, entity.getDescription());
            stmt.setString(4, entity.getPriority().name());
            stmt.setString(5, entity.getStatus().name());

            stmt.executeUpdate();
            Long generatedId = getGeneratedId(stmt);
            if (generatedId != null) {
                entity.setId(generatedId);
            }
            return entity;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save ticket", e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public void update(SupportTicket entity) {
        String sql = "UPDATE support_tickets SET title = ?, description = ?, priority = ?, status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entity.getTitle());
            stmt.setString(2, entity.getDescription());
            stmt.setString(3, entity.getPriority().name());
            stmt.setString(4, entity.getStatus().name());
            stmt.setLong(5, entity.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update ticket: " + entity.getId(), e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM support_tickets WHERE id = ?";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete ticket: " + id, e);
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM support_tickets";
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count tickets", e);
        } finally {
            releaseConnection(conn);
        }
        return 0L;
    }

    private SupportTicket mapRow(ResultSet rs) throws SQLException {
        SupportTicket st = new SupportTicket();
        st.setId(rs.getLong("id"));
        st.setCreatedBy(rs.getLong("created_by"));
        st.setTitle(rs.getString("title"));
        st.setDescription(rs.getString("description"));

        String prio = rs.getString("priority");
        try {
            st.setPriority(TicketPriority.valueOf(prio));
        } catch (Exception ignored) {
            st.setPriority(TicketPriority.MEDIUM);
        }

        String stat = rs.getString("status");
        try {
            st.setStatus(TicketStatus.valueOf(stat));
        } catch (Exception ignored) {
            st.setStatus(TicketStatus.OPEN);
        }

        st.setCreatedAt(toLocalDateTime(rs.getTimestamp("created_at")));
        st.setUpdatedAt(toLocalDateTime(rs.getTimestamp("updated_at")));
        return st;
    }
}
