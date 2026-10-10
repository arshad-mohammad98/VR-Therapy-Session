package com.vrtherapy.observer;

/**
 * Observer interface for reacting to crisis alerts, device disconnections, and safety thresholds.
 * Demonstrates the Observer Design Pattern.
 */
@FunctionalInterface
public interface AlertListener {

    void onAlert(AlertEvent event);
}
