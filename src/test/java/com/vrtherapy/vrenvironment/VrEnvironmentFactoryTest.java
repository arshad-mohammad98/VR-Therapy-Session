package com.vrtherapy.vrenvironment;

import com.vrtherapy.factory.VrEnvironmentFactory;
import com.vrtherapy.model.EnvironmentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class VrEnvironmentFactoryTest {

    @Test
    @DisplayName("Factory should return all registered procedural environments")
    void testFactoryReturnsAllEnvironments() {
        assertEquals(6, VrEnvironmentFactory.getAllEnvironments().size());
        assertNotNull(VrEnvironmentFactory.getEnvironment(EnvironmentType.CALM_BEACH));
        assertNotNull(VrEnvironmentFactory.getEnvironment(EnvironmentType.FOREST_WALK));
        assertNotNull(VrEnvironmentFactory.getEnvironment(EnvironmentType.HEIGHTS_EXPOSURE));
        assertNotNull(VrEnvironmentFactory.getEnvironment(EnvironmentType.SOCIAL_GATHERING));
        assertNotNull(VrEnvironmentFactory.getEnvironment(EnvironmentType.BREATHING_ROOM));
        assertNotNull(VrEnvironmentFactory.getEnvironment(EnvironmentType.NIGHT_SKY));
    }

    @Test
    @DisplayName("BeachEnvironment should generate comprehensive WebXR configuration map")
    void testBeachEnvironmentGeneratesValidConfig() {
        VrEnvironment beach = VrEnvironmentFactory.getEnvironment(EnvironmentType.CALM_BEACH);
        Map<String, Object> config = beach.generateSceneConfiguration(2, true, true);

        assertEquals("CALM_BEACH", config.get("environmentType"));
        assertEquals(2, config.get("intensity"));
        assertNotNull(config.get("skyColor"));
        assertNotNull(config.get("groundColor"));
        assertEquals("ocean_waves", config.get("ambientSound"));
        assertTrue((Boolean) config.get("guidedBreathingEnabled"));
        assertNotNull(config.get("breathingGuide"));
    }

    @Test
    @DisplayName("HeightsEnvironment adjusts deck platform elevation according to intensity")
    void testHeightsEnvironmentGeneratesValidConfig() {
        HeightsEnvironment heights = (HeightsEnvironment) VrEnvironmentFactory.getEnvironment(EnvironmentType.HEIGHTS_EXPOSURE);
        Map<String, Object> config = heights.generateSceneConfiguration(4, false, true);

        assertEquals(4, config.get("intensity"));
        assertNotNull(config.get("platformHeightMeters"));
        double elevation = (Double) config.get("platformHeightMeters");
        assertEquals(110.0, elevation); // 10.0 + (4 * 25.0) = 110.0
    }

    @Test
    @DisplayName("BeachEnvironment adjusts intensity down when patient has high post-session stress")
    void testBeachIntensityAdjustmentDownOnHighStress() {
        VrEnvironment beach = VrEnvironmentFactory.getEnvironment(EnvironmentType.CALM_BEACH);
        int currentIntensity = 3;
        int adjusted = beach.adjustIntensityForPatient(currentIntensity, -1, 8);
        assertEquals(2, adjusted);
    }
}
