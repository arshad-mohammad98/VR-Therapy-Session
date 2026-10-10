package com.vrtherapy.exception;

/**
 * Checked exception thrown when database operations encounter unrecoverable SQL errors.
 */
public class DatabaseException extends RuntimeException {
    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
