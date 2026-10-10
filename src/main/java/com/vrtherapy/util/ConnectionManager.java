package com.vrtherapy.util;

import com.vrtherapy.exception.DatabaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Thread-safe Singleton Connection Manager implementing a custom JDBC connection pool
 * backed by a {@link BlockingQueue} and {@link ReentrantLock}.
 *
 * Demonstrates:
 * - Singleton Design Pattern with Double-Checked Locking
 * - Multithreading & Synchronization (BlockingQueue, AtomicInteger, ReentrantLock)
 * - Raw JDBC Configuration and Driver initialization
 * - Automatic schema bootstrapping for embedded zero-setup mode
 */
public class ConnectionManager {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionManager.class);
    private static volatile ConnectionManager instance;
    private static final Object LOCK = new Object();

    private final Properties props = new Properties();
    private final BlockingQueue<Connection> pool;
    private final AtomicInteger activeConnections = new AtomicInteger(0);
    private final ReentrantLock poolLock = new ReentrantLock();

    private String dbDriver;
    private String dbUrl;
    private String dbUser;
    private String dbPassword;
    private int initialSize = 5;
    private int maxSize = 20;
    private long connectionTimeoutMs = 5000;
    private volatile boolean isShutdown = false;

    private ConnectionManager() {
        loadConfiguration();
        initializeDriver();
        this.pool = new ArrayBlockingQueue<>(maxSize);
        initializePool();
        bootstrapDatabaseIfNecessary();
    }

    /**
     * Thread-safe Singleton instance retrieval.
     */
    public static ConnectionManager getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new ConnectionManager();
                }
            }
        }
        return instance;
    }

    /**
     * Loads db.properties from classpath or current working directory.
     */
    private void loadConfiguration() {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                props.load(in);
                logger.info("Loaded db.properties successfully from classpath.");
            } else {
                logger.warn("db.properties not found in classpath; applying embedded defaults.");
            }
        } catch (Exception e) {
            logger.error("Error reading db.properties: {}", e.getMessage(), e);
        }

        String mode = props.getProperty("db.mode", "embedded");
        if ("mysql".equalsIgnoreCase(mode)) {
            this.dbDriver = props.getProperty("db.mysql.driver", "com.mysql.cj.jdbc.Driver");
            this.dbUrl = props.getProperty("db.mysql.url", "jdbc:mysql://localhost:3306/vrtherapy_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
            this.dbUser = props.getProperty("db.mysql.user", "root");
            this.dbPassword = props.getProperty("db.mysql.password", "root");
        } else {
            this.dbDriver = props.getProperty("db.embedded.driver", "org.h2.Driver");
            this.dbUrl = props.getProperty("db.embedded.url", "jdbc:h2:mem:vrtherapy_db;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;DB_CLOSE_DELAY=-1");
            this.dbUser = props.getProperty("db.embedded.user", "sa");
            this.dbPassword = props.getProperty("db.embedded.password", "");
        }

        this.initialSize = Integer.parseInt(props.getProperty("pool.initialSize", "5"));
        this.maxSize = Integer.parseInt(props.getProperty("pool.maxSize", "20"));
        this.connectionTimeoutMs = Long.parseLong(props.getProperty("pool.connectionTimeoutMs", "5000"));
        logger.info("Database configured in [{}] mode: URL={}", mode, dbUrl);
    }

    private void initializeDriver() {
        try {
            Class.forName(dbDriver);
            logger.info("Registered JDBC driver: {}", dbDriver);
        } catch (ClassNotFoundException e) {
            throw new DatabaseException("Failed to find JDBC driver: " + dbDriver, e);
        }
    }

    private void initializePool() {
        poolLock.lock();
        try {
            for (int i = 0; i < initialSize; i++) {
                Connection conn = createNewConnection();
                pool.offer(conn);
                activeConnections.incrementAndGet();
            }
            logger.info("Connection pool initialized with {} baseline connections.", pool.size());
        } catch (SQLException e) {
            throw new DatabaseException("Unable to establish initial database connection pool", e);
        } finally {
            poolLock.unlock();
        }
    }

    private Connection createNewConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }

    /**
     * Borrows a connection from the pool, waiting up to the configured timeout.
     * Demonstrates concurrency synchronization with BlockingQueue.
     */
    public Connection getConnection() {
        if (isShutdown) {
            throw new DatabaseException("Connection pool is shutdown");
        }
        try {
            Connection conn = pool.poll(500, TimeUnit.MILLISECONDS);
            if (conn != null && !conn.isClosed() && conn.isValid(2)) {
                return conn;
            }

            // If pool has room for growth
            poolLock.lock();
            try {
                if (activeConnections.get() < maxSize) {
                    Connection newConn = createNewConnection();
                    activeConnections.incrementAndGet();
                    return newConn;
                }
            } finally {
                poolLock.unlock();
            }

            // Wait for available connection up to configured timeout
            conn = pool.poll(connectionTimeoutMs, TimeUnit.MILLISECONDS);
            if (conn == null) {
                throw new DatabaseException("Database connection timeout: pool exhausted (" + activeConnections.get() + " active)");
            }
            if (conn.isClosed() || !conn.isValid(2)) {
                return createNewConnection();
            }
            return conn;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new DatabaseException("Interrupted while waiting for database connection", e);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to obtain database connection", e);
        }
    }

    /**
     * Releases a borrowed connection back to the custom connection pool.
     */
    public void releaseConnection(Connection connection) {
        if (connection == null) {
            return;
        }
        if (isShutdown) {
            try {
                connection.close();
            } catch (SQLException ignored) {
            }
            return;
        }
        try {
            if (!connection.isClosed()) {
                if (!connection.getAutoCommit()) {
                    connection.setAutoCommit(true);
                }
                boolean returned = pool.offer(connection);
                if (!returned) {
                    // Pool queue is full, close excess connection
                    connection.close();
                    activeConnections.decrementAndGet();
                }
            } else {
                activeConnections.decrementAndGet();
            }
        } catch (SQLException e) {
            logger.warn("Error releasing connection: {}", e.getMessage());
            activeConnections.decrementAndGet();
        }
    }

    /**
     * Executes transactional logic with automatic commit on success and rollback on failure.
     * Demonstrates JDBC transactions, PreparedStatement safety, and try-with-resources.
     */
    public <T> T executeTransaction(TransactionCallback<T> action) {
        Connection conn = getConnection();
        try {
            conn.setAutoCommit(false);
            T result = action.doInTransaction(conn);
            conn.commit();
            return result;
        } catch (Exception e) {
            try {
                conn.rollback();
                logger.warn("Transaction rolled back due to error: {}", e.getMessage());
            } catch (SQLException rollbackEx) {
                logger.error("Failed to rollback transaction: {}", rollbackEx.getMessage(), rollbackEx);
            }
            throw new DatabaseException("Transaction execution failed: " + e.getMessage(), e);
        } finally {
            releaseConnection(conn);
        }
    }

    /**
     * Bootstraps schema.sql and seed.sql when tables are absent.
     */
    private void bootstrapDatabaseIfNecessary() {
        Connection conn = null;
        try {
            conn = getConnection();
            // Check if users table exists
            boolean tablesExist = false;
            try (Statement stmt = conn.createStatement()) {
                stmt.executeQuery("SELECT 1 FROM users LIMIT 1");
                tablesExist = true;
            } catch (SQLException ignored) {
                // Table does not exist
            }

            if (!tablesExist) {
                logger.info("Initializing database schema and seed records...");
                executeSqlScript(conn, "schema.sql");
                executeSqlScript(conn, "seed.sql");
                logger.info("Database bootstrapped successfully.");
            }
        } catch (Exception e) {
            logger.warn("Note during bootstrap check: {}", e.getMessage());
        } finally {
            if (conn != null) {
                releaseConnection(conn);
            }
        }
    }

    private void executeSqlScript(Connection conn, String scriptName) {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(scriptName)) {
            if (in == null) {
                logger.warn("Script {} not found in classpath.", scriptName);
                return;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
                 Statement stmt = conn.createStatement()) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("--")) {
                        continue;
                    }
                    sb.append(line).append(" ");
                    if (line.endsWith(";")) {
                        String sql = sb.toString();
                        sql = sql.substring(0, sql.length() - 1).trim();
                        if (!sql.isEmpty()) {
                            try {
                                stmt.execute(sql);
                            } catch (SQLException sqlEx) {
                                logger.debug("Statement notice ({}): {}", scriptName, sqlEx.getMessage());
                            }
                        }
                        sb.setLength(0);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Failed to execute script {}: {}", scriptName, e.getMessage(), e);
        }
    }

    /**
     * Closes all connections gracefully during application shutdown.
     */
    public void shutdown() {
        isShutdown = true;
        poolLock.lock();
        try {
            Connection conn;
            while ((conn = pool.poll()) != null) {
                try {
                    conn.close();
                } catch (SQLException ignored) {
                }
            }
            activeConnections.set(0);
            logger.info("Database connection pool safely shut down.");
        } finally {
            poolLock.unlock();
        }
    }

    @FunctionalInterface
    public interface TransactionCallback<T> {
        T doInTransaction(Connection connection) throws Exception;
    }
}
