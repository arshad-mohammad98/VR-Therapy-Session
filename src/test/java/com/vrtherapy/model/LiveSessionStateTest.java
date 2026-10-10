package com.vrtherapy.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LiveSessionStateTest {

    @Test
    @DisplayName("State Machine: allows valid sequential transitions")
    void testValidTransitions() {
        assertTrue(LiveSessionState.SCHEDULED.canTransitionTo(LiveSessionState.WAITING));
        assertTrue(LiveSessionState.SCHEDULED.canTransitionTo(LiveSessionState.ACTIVE));
        assertTrue(LiveSessionState.WAITING.canTransitionTo(LiveSessionState.ACTIVE));
        assertTrue(LiveSessionState.ACTIVE.canTransitionTo(LiveSessionState.PAUSED));
        assertTrue(LiveSessionState.PAUSED.canTransitionTo(LiveSessionState.ACTIVE));
        assertTrue(LiveSessionState.ACTIVE.canTransitionTo(LiveSessionState.COMPLETED));
        assertTrue(LiveSessionState.PAUSED.canTransitionTo(LiveSessionState.COMPLETED));
    }

    @Test
    @DisplayName("State Machine: rejects illegal skipping or reverse transitions")
    void testInvalidTransitions() {
        assertFalse(LiveSessionState.SCHEDULED.canTransitionTo(LiveSessionState.COMPLETED));
        assertFalse(LiveSessionState.COMPLETED.canTransitionTo(LiveSessionState.ACTIVE));
        assertFalse(LiveSessionState.COMPLETED.canTransitionTo(LiveSessionState.SCHEDULED));
        assertFalse(LiveSessionState.EMERGENCY_STOPPED.canTransitionTo(LiveSessionState.ACTIVE));
        assertFalse(LiveSessionState.ACTIVE.canTransitionTo(null));
    }

    @Test
    @DisplayName("Safety Protocol: allows Emergency Stop from any active/paused/waiting/scheduled state")
    void testEmergencyStopAllowedEverywhere() {
        assertTrue(LiveSessionState.SCHEDULED.canTransitionTo(LiveSessionState.EMERGENCY_STOPPED));
        assertTrue(LiveSessionState.WAITING.canTransitionTo(LiveSessionState.EMERGENCY_STOPPED));
        assertTrue(LiveSessionState.ACTIVE.canTransitionTo(LiveSessionState.EMERGENCY_STOPPED));
        assertTrue(LiveSessionState.PAUSED.canTransitionTo(LiveSessionState.EMERGENCY_STOPPED));
    }
}
