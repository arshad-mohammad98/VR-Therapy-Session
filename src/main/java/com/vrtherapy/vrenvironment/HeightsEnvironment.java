package com.vrtherapy.vrenvironment;

import com.vrtherapy.model.EnvironmentType;
import java.util.Map;

/**
 * Heights Exposure environment tailored for graded exposure therapy against Acrophobia.
 */
public class HeightsEnvironment extends AbstractVrEnvironment {

    public HeightsEnvironment() {
        super(EnvironmentType.HEIGHTS_EXPOSURE, "Acrophobia High-Altitude Deck");
    }

    @Override
    public String getSkyColor(int intensity) {
        return switch (intensity) {
            case 1 -> "#d4e6f1";
            case 2 -> "#aed6f1";
            case 3 -> "#85c1e9";
            case 4 -> "#5dade2";
            default -> "#3498db";
        };
    }

    @Override
    public String getGroundColor() {
        return "#566573"; // High observation glass/metal deck
    }

    @Override
    public String getAmbientSoundAsset() {
        return "high_altitude_wind";
    }

    @Override
    public String getAtmosphereDescription() {
        return "Modern high-rise architectural platform elevated above a scenic landscape with adjustable railings.";
    }

    @Override
    protected void populateCustomEntities(Map<String, Object> config, int intensity) {
        // Platform elevation scales from 10m (intensity 1) to 120m (intensity 5)
        double deckElevationMeters = 10.0 + (intensity * 25.0);
        config.put("platformHeightMeters", deckElevationMeters);
        config.put("railingTransparency", Math.max(0.2, 1.0 - (intensity * 0.18)));
        config.put("cityGridBelow", true);
        config.put("windGustiness", intensity * 0.2);
    }

    @Override
    public int adjustIntensityForPatient(int currentIntensity, int stressDelta, int postStressRating) {
        // High post stress or negative delta in exposure therapy triggers systematic desensitization step down
        if (postStressRating >= 8 || stressDelta < -1) {
            return Math.max(1, currentIntensity - 1);
        } else if (postStressRating <= 4 && stressDelta >= 2 && currentIntensity < 5) {
            return Math.min(5, currentIntensity + 1);
        }
        return currentIntensity;
    }
}
