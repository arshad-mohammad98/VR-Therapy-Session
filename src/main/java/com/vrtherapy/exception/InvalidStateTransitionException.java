package com.vrtherapy.exception;

/**
 * Exception thrown when a state machine transition is illegal (e.g., SCHEDULED directly to COMPLETED).
 */
public class InvalidStateTransitionException extends RuntimeException {
    public InvalidStateTransitionException(String message) {
        super(message);
    }
}
