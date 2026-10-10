package com.vrtherapy.exception;

import java.util.Collections;
import java.util.List;

/**
 * Exception thrown when domain validation constraints are violated.
 */
public class ValidationException extends RuntimeException {
    private final List<String> errorMessages;

    public ValidationException(String message) {
        super(message);
        this.errorMessages = Collections.singletonList(message);
    }

    public ValidationException(List<String> errorMessages) {
        super(String.join(", ", errorMessages));
        this.errorMessages = errorMessages;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }
}
