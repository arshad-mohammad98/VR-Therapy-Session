package com.vrtherapy.vrenvironment;

import com.vrtherapy.model.EnvironmentType;
import java.util.Map;

/**
 * Night Sky Sleep Room environment designed for insomnia, somnology relaxation, and evening restorative decompression.
 */
public class NightSkyEnvironment extends AbstractVrEnvironment {

    public NightSkyEnvironment() {
        super(EnvironmentType.NIGHT_SKY_SLEEP_ROOM, "Celestial Night Sky & Starlit Meadow");
    }

    @Override
    public String getSkyColor(int intensity) {
        return switch (intensity) {
            case 1 -> "#02040a";
            case 2 -> "#050b14";
            case 3 -> "#0b1528";
            case 4 -> "#101d36";
            default -> "#162238";
        };
    }

    @Override
    public String getGroundColor() {
        return "#0a1118"; // Deep midnight meadow floor
    }

    @Override
    public String getAmbientSoundAsset() {
        return "night_crickets_celestial_hum";
    }

    @Override
    public String getAtmosphereDescription() {
        return "Vast twilight horizon, rotating constellations, subtle aurora borealis, gentle night breeze, and slow shooting stars.";
    }

    @Override
    protected void populateCustomEntities(Map<String, Object> config, int intensity) {
        config.put("auroraColor", intensity <= 2 ? "#5eead4" : "#818cf8");
        config.put("shootingStarFrequencySec", 12 - (intensity * 2));
        config.put("moonPhase", "crescent");
        config.put("nebulaVisible", true);
        config.put("starlightTwinkle", true);
    }

    @Override
    public int adjustIntensityForPatient(int currentIntensity, int stressDelta, int postStressRating) {
        // Evening sleep environment: always bias towards lowest sensory stimulation if patient remains stressed
        if (postStressRating > 4 || stressDelta >= 0) {
            return Math.max(1, currentIntensity - 1);
        }
        return currentIntensity;
    }
}
