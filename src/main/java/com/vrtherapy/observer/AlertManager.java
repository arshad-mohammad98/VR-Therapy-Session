package com.vrtherapy.observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Thread-safe Subject (Publisher) in the Observer Design Pattern.
 * Manages active subscribers and broadcasts alerts.
 *
 * Demonstrates:
 * - Observer Pattern
 * - Concurrency with CopyOnWriteArrayList
 */
public class AlertManager {

    private static final Logger logger = LoggerFactory.getLogger(AlertManager.class);
    private static final AlertManager INSTANCE = new AlertManager();

    private final List<AlertListener> listeners = new CopyOnWriteArrayList<>();

    private AlertManager() {
        // Default system log listener
        registerListener(event -> logger.warn("[SYSTEM ALERT] {}: {} (Patient ID: {})",
                event.getAlertType(), event.getMessage(), event.getPatientId()));
    }

    public static AlertManager getInstance() {
        return INSTANCE;
    }

    public void registerListener(AlertListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    public void unregisterListener(AlertListener listener) {
        listeners.remove(listener);
    }

    public void broadcastAlert(String type, String message, Long patientId) {
        AlertEvent event = new AlertEvent(type, message, patientId);
        for (AlertListener listener : listeners) {
            try {
                listener.onAlert(event);
            } catch (Exception e) {
                logger.error("Error in alert listener execution: {}", e.getMessage(), e);
            }
        }
    }
}
