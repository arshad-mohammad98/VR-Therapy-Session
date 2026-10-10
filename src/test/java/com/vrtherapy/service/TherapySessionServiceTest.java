package com.vrtherapy.service;

import com.vrtherapy.dao.SessionDao;
import com.vrtherapy.dto.TherapySessionDto;
import com.vrtherapy.exception.ValidationException;
import com.vrtherapy.model.*;
import com.vrtherapy.service.impl.TherapySessionServiceImpl;
import com.vrtherapy.strategy.SessionAdjustmentSuggestion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TherapySessionServiceTest {

    @Mock
    private SessionDao sessionDao;

    @InjectMocks
    private TherapySessionServiceImpl sessionService;

    private Therapist therapist;

    @BeforeEach
    void setUp() {
        therapist = new Therapist("therapist1", "pass", "Dr. Elena", "elena@example.com", true);
        therapist.setId(1L);
    }

    @Test
    @DisplayName("Should create session and generate valid JSON scene configuration")
    void testCreateSessionGeneratesValidJsonConfiguration() {
        TherapySessionDto dto = new TherapySessionDto();
        dto.setTitle("Calm Beach Horizon");
        dto.setConditionType(ConditionType.ANXIETY);
        dto.setEnvironmentType(EnvironmentType.CALM_BEACH);
        dto.setDurationMinutes(25);
        dto.setIntensityLevel(2);
        dto.setBackgroundAudioEnabled(true);
        dto.setGuidedBreathingEnabled(true);

        when(sessionDao.save(any(TherapySession.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TherapySession created = sessionService.createSession(dto, therapist);

        assertNotNull(created);
        assertEquals("Calm Beach Horizon", created.getTitle());
        assertNotNull(created.getConfigJson());
        assertTrue(created.getConfigJson().contains("CALM_BEACH"));
    }

    @Test
    @DisplayName("Should throw ValidationException for invalid duration or intensity")
    void testInvalidSessionParametersThrowsValidationException() {
        TherapySessionDto invalidDto = new TherapySessionDto();
        invalidDto.setTitle("Invalid Session");
        invalidDto.setDurationMinutes(2); // Below minimum 5
        invalidDto.setIntensityLevel(9);  // Above max 5

        assertThrows(ValidationException.class, () -> sessionService.createSession(invalidDto, therapist));
    }

    @Test
    @DisplayName("Should apply clinical suggestion adjustment directly to session")
    void testApplySuggestionAdjustmentUpdatesSessionCorrectly() {
        TherapySession session = new TherapySession(
                "Beach Session",
                ConditionType.ANXIETY,
                EnvironmentType.CALM_BEACH,
                30,
                4,
                true,
                false,
                "Initial",
                1L
        );
        session.setId(5L);

        when(sessionDao.findById(5L)).thenReturn(Optional.of(session));
        doNothing().when(sessionDao).update(any(TherapySession.class));

        SessionAdjustmentSuggestion suggestion = new SessionAdjustmentSuggestion(
                "HighStressRegulationRule", "Reduce Intensity", "Lower stimulus level",
                "Clinical safety", "REDUCE_INTENSITY", 2, 20, true, true
        );

        TherapySession adjusted = sessionService.applySuggestionAdjustment(5L, suggestion, therapist);

        assertEquals(2, adjusted.getIntensityLevel());
        assertEquals(20, adjusted.getDefaultDurationMinutes());
        assertTrue(adjusted.isGuidedBreathingEnabled());
        assertTrue(adjusted.getConfigJson().contains("\"intensity\":2"));
    }
}
