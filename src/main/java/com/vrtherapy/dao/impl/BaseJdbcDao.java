package com.vrtherapy.dao.impl;

import com.vrtherapy.util.ConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Base abstract JDBC class providing connection lifecycle helpers and timestamp utilities.
 * Demonstrates OOP Abstraction and raw JDBC best practices.
 */
public abstract class BaseJdbcDao {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected Connection getConnection() {
        return ConnectionManager.getInstance().getConnection();
    }

    protected void releaseConnection(Connection connection) {
        ConnectionManager.getInstance().releaseConnection(connection);
    }

    protected Timestamp toTimestamp(LocalDateTime ldt) {
        return ldt != null ? Timestamp.valueOf(ldt) : null;
    }

    protected LocalDateTime toLocalDateTime(Timestamp ts) {
        return ts != null ? ts.toLocalDateTime() : null;
    }

    protected Date toSqlDate(LocalDate ld) {
        return ld != null ? Date.valueOf(ld) : null;
    }

    protected LocalDate toLocalDate(Date d) {
        return d != null ? d.toLocalDate() : null;
    }

    protected Long getGeneratedId(PreparedStatement stmt) throws SQLException {
        try (ResultSet rs = stmt.getGeneratedKeys()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        }
        return null;
    }
}
