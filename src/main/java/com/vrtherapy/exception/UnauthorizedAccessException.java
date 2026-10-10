package com.vrtherapy.exception;

/**
 * Exception thrown when a user attempts an action outside their granted role or ownership scope.
 */
public class UnauthorizedAccessException extends RuntimeException {

    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
