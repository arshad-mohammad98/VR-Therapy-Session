package com.vrtherapy.strategy;

import com.vrtherapy.model.LiveSession;
import com.vrtherapy.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CrisisDetectionStrategyTest {

    private HeartRateThresholdCrisisStrategy hrStrategy;
    private SubjectiveStressCrisisStrategy stressStrategy;
    private ProlongedElevationCrisisStrategy prolongedStrategy;
    private CrisisEngine crisisEngine;

    @BeforeEach
    void setUp() {
        hrStrategy = new HeartRateThresholdCrisisStrategy();
        stressStrategy = new SubjectiveStressCrisisStrategy();
        prolongedStrategy = new ProlongedElevationCrisisStrategy();
        crisisEngine = new CrisisEngine(List.of(hrStrategy, stressStrategy, prolongedStrategy));
    }

    @Test
    @DisplayName("HR Threshold: triggers crisis alert when heart rate reaches 135+ bpm")
    void testHeartRateCrisisTrigger() {
        CrisisContext highHr = new CrisisContext(null, null, 138, 5, 1);
        CrisisContext normalHr = new CrisisContext(null, null, 110, 5, 1);

        assertTrue(hrStrategy.evaluate(highHr));
        assertFalse(hrStrategy.evaluate(normalHr));
        assertTrue(hrStrategy.getAlertMessage(highHr).contains("Heart rate surged"));
    }

    @Test
    @DisplayName("Subjective Stress: triggers crisis alert when score is 9 or 10")
    void testSubjectiveStressCrisisTrigger() {
        CrisisContext severeStress = new CrisisContext(null, null, 80, 9, 1);
        CrisisContext moderateStress = new CrisisContext(null, null, 80, 7, 1);

        assertTrue(stressStrategy.evaluate(severeStress));
        assertFalse(stressStrategy.evaluate(moderateStress));
        assertTrue(stressStrategy.getAlertMessage(severeStress).contains("acute panic"));
    }

    @Test
    @DisplayName("Prolonged Elevation: triggers when HR > 115 and stress >= 7 for 3+ consecutive pulses")
    void testProlongedElevationTrigger() {
        CrisisContext sustained = new CrisisContext(null, null, 120, 8, 3);
        CrisisContext transientSpike = new CrisisContext(null, null, 120, 8, 1);

        assertTrue(prolongedStrategy.evaluate(sustained));
        assertFalse(prolongedStrategy.evaluate(transientSpike));
    }

    @Test
    @DisplayName("Crisis Engine: returns empty optional when biometrics are stable")
    void testCrisisEngineStable() {
        CrisisContext stable = new CrisisContext(null, null, 72, 3, 0);
        Optional<String> alert = crisisEngine.evaluateCrisis(stable);
        assertTrue(alert.isEmpty());
    }

    @Test
    @DisplayName("Crisis Engine: returns first triggered alert when crisis threshold breached")
    void testCrisisEngineAlertReturned() {
        CrisisContext spiked = new CrisisContext(null, null, 140, 9, 1);
        Optional<String> alert = crisisEngine.evaluateCrisis(spiked);
        assertTrue(alert.isPresent());
        assertTrue(alert.get().contains("Heart rate surged"));
    }
}
