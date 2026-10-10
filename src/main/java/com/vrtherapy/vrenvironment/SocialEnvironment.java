package com.vrtherapy.vrenvironment;

import com.vrtherapy.model.EnvironmentType;
import java.util.Map;

/**
 * Social Gathering environment tailored for Social Anxiety Disorder exposure and comfort desensitization.
 */
public class SocialEnvironment extends AbstractVrEnvironment {

    public SocialEnvironment() {
        super(EnvironmentType.SOCIAL_GATHERING, "Open Terrace Social Lounge");
    }

    @Override
    public String getSkyColor(int intensity) {
        return switch (intensity) {
            case 1 -> "#f9e79f"; // Warm sunset glow
            case 2 -> "#f8c471";
            case 3 -> "#eb984e";
            case 4 -> "#e59866";
            default -> "#d35400";
        };
    }

    @Override
    public String getGroundColor() {
        return "#795548"; // Terrace wood decking
    }

    @Override
    public String getAmbientSoundAsset() {
        return "ambient_cafe_murmur";
    }

    @Override
    public String getAtmosphereDescription() {
        return "Cozy outdoor courtyard terrace with spaced bistro tables, warm fairy string lights, and calm ambient chatter.";
    }

    @Override
    protected void populateCustomEntities(Map<String, Object> config, int intensity) {
        int avatarCount = 2 + (intensity * 3); // 5 up to 17 simulated avatars
        config.put("avatarCount", avatarCount);
        config.put("proximityMeters", Math.max(1.5, 5.0 - (intensity * 0.7)));
        config.put("stringLights", true);
        config.put("cafeTablesCount", 4 + intensity);
    }

    @Override
    public int adjustIntensityForPatient(int currentIntensity, int stressDelta, int postStressRating) {
        if (postStressRating >= 7 || stressDelta < 0) {
            return Math.max(1, currentIntensity - 1);
        }
        return currentIntensity;
    }
}
