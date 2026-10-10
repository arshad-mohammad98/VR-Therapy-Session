package com.vrtherapy.vrenvironment;

import com.vrtherapy.model.EnvironmentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class NightSkyEnvironmentTest {

    private NightSkyEnvironment environment;

    @BeforeEach
    void setUp() {
        environment = new NightSkyEnvironment();
    }

    @Test
    @DisplayName("NightSky: has correct EnvironmentType and sensory parameters")
    void testSensoryParameters() {
        assertEquals(EnvironmentType.NIGHT_SKY_SLEEP_ROOM, environment.getType());
        assertNotNull(environment.getSkyColor(1));
        assertEquals("#0a1118", environment.getGroundColor());
        assertEquals("night_crickets_celestial_hum", environment.getAmbientSoundAsset());
        assertTrue(environment.getAtmosphereDescription().contains("twilight"));
    }

    @Test
    @DisplayName("NightSky: builds valid configuration map")
    void testBuildConfig() {
        Map<String, Object> config = environment.generateSceneConfiguration(2, true, true);
        assertEquals("NIGHT_SKY_SLEEP_ROOM", config.get("environmentType"));
        assertEquals("crescent", config.get("moonPhase"));
        assertTrue((Boolean) config.get("nebulaVisible"));
        assertTrue((Boolean) config.get("starlightTwinkle"));
    }

    @Test
    @DisplayName("NightSky: reduces intensity when post-session stress remains elevated")
    void testIntensityAdjustment() {
        int adjusted = environment.adjustIntensityForPatient(3, 1, 6);
        assertEquals(2, adjusted, "Should step down sensory intensity for stressed sleep patient");
    }
}
