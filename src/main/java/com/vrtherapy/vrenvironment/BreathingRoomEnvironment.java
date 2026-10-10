package com.vrtherapy.vrenvironment;

import com.vrtherapy.model.EnvironmentType;
import java.util.Map;

/**
 * Breathing Room sanctuary environment focused on biofeedback, guided breath pacing, and deep meditative grounding.
 */
public class BreathingRoomEnvironment extends AbstractVrEnvironment {

    public BreathingRoomEnvironment() {
        super(EnvironmentType.BREATHING_ROOM, "Biofeedback Breathing Sanctuary");
    }

    @Override
    public String getSkyColor(int intensity) {
        return switch (intensity) {
            case 1 -> "#0d1b2a";
            case 2 -> "#1b263b";
            case 3 -> "#212f45";
            case 4 -> "#273852";
            default -> "#2d4059";
        };
    }

    @Override
    public String getGroundColor() {
        return "#111927"; // Dark reflective obsidian floor
    }

    @Override
    public String getAmbientSoundAsset() {
        return "binaural_relaxing_drone";
    }

    @Override
    public String getAtmosphereDescription() {
        return "Zen sanctuary with reflective water floor, starfield ceiling, and a central glowing breathing orb.";
    }

    @Override
    protected void populateCustomEntities(Map<String, Object> config, int intensity) {
        config.put("orbGlowColor", intensity > 3 ? "#00e676" : "#64ffda");
        config.put("floatingParticlesCount", 40 + (intensity * 20));
        config.put("starfield", true);
        config.put("waterReflection", true);
    }

    @Override
    public int adjustIntensityForPatient(int currentIntensity, int stressDelta, int postStressRating) {
        // Breathing room is therapeutic and restorative
        if (postStressRating > 5) {
            return Math.max(1, currentIntensity - 1);
        }
        return currentIntensity;
    }
}
