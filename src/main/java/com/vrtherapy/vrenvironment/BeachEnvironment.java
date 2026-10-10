package com.vrtherapy.vrenvironment;

import com.vrtherapy.model.EnvironmentType;
import java.util.Map;

/**
 * Calm Beach WebXR environment tailored for somatic anxiety reduction, panic relief, and deep breathing.
 */
public class BeachEnvironment extends AbstractVrEnvironment {

    public BeachEnvironment() {
        super(EnvironmentType.CALM_BEACH, "Calm Beach Sanctuary");
    }

    @Override
    public String getSkyColor(int intensity) {
        // From serene pastel sunrise (intensity 1) to bright tropical midday sun (intensity 5)
        return switch (intensity) {
            case 1 -> "#a8d8ea";
            case 2 -> "#87ceeb";
            case 3 -> "#6cb2eb";
            case 4 -> "#4ba3e3";
            default -> "#3892d0";
        };
    }

    @Override
    public String getGroundColor() {
        return "#e8d5b5"; // Warm shoreline sand
    }

    @Override
    public String getAmbientSoundAsset() {
        return "ocean_waves";
    }

    @Override
    public String getAtmosphereDescription() {
        return "Gentle turquoise ocean surf rolling over fine golden sand with tropical warm breeze.";
    }

    @Override
    protected void populateCustomEntities(Map<String, Object> config, int intensity) {
        config.put("waterPlaneColor", "#4da6ff");
        config.put("waveSpeed", 0.5 + (intensity * 0.15));
        config.put("sunPosition", "0 15 -30");
        config.put("palmTreesCount", 8);
        config.put("particles", "sea_spray");
    }

    @Override
    public int adjustIntensityForPatient(int currentIntensity, int stressDelta, int postStressRating) {
        if (postStressRating >= 7 || stressDelta < 0) {
            // Patient grew more anxious: reduce intensity
            return Math.max(1, currentIntensity - 1);
        } else if (postStressRating <= 3 && stressDelta >= 2 && currentIntensity < 5) {
            // Patient responded very well: can slightly increase challenge if desired
            return Math.min(5, currentIntensity + 1);
        }
        return currentIntensity;
    }
}
