package com.vrtherapy.thread;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Command message dispatched by therapist to live WebXR patient session.
 */
public class VrCommand implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum CommandType {
        CHANGE_INTENSITY,
        CHANGE_ENVIRONMENT,
        START_BREATHING,
        STOP_BREATHING,
        SHOW_MESSAGE,
        PAUSE_SESSION,
        RESUME_SESSION,
        EMERGENCY_STOP
    }

    private final String id;
    private final CommandType type;
    private final String payload;
    private final LocalDateTime timestamp;

    public VrCommand(CommandType type, String payload) {
        this.id = java.util.UUID.randomUUID().toString().substring(0, 8);
        this.type = type;
        this.payload = payload;
        this.timestamp = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public CommandType getType() {
        return type;
    }

    public String getPayload() {
        return payload;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "VrCommand{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", payload='" + payload + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
