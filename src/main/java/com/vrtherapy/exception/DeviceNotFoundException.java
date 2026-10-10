package com.vrtherapy.exception;

/**
 * Exception thrown when a requested VR device or pairing code cannot be found.
 */
public class DeviceNotFoundException extends RuntimeException {
    public DeviceNotFoundException(String message) {
        super(message);
    }
}
