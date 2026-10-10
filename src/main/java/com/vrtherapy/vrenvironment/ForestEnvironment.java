package com.vrtherapy.vrenvironment;

import com.vrtherapy.model.EnvironmentType;
import java.util.Map;

/**
 * Forest Walk environment designed for grounding, depressive apathy relief, and mindfulness walk.
 */
public class ForestEnvironment extends AbstractVrEnvironment {

    public ForestEnvironment() {
        super(EnvironmentType.FOREST_WALK, "Lush Forest Canopy");
    }

    @Override
    public String getSkyColor(int intensity) {
        return switch (intensity) {
            case 1 -> "#cbe3db";
            case 2 -> "#b3d9cc";
            case 3 -> "#99ccbb";
            case 4 -> "#80bfaa";
            default -> "#66b299";
        };
    }

    @Override
    public String getGroundColor() {
        return "#3d5a3a"; // Forest moss and earthen path
    }

    @Override
    public String getAmbientSoundAsset() {
        return "forest_birds_wind";
    }

    @Override
    public String getAtmosphereDescription() {
        return "Pine woodland path dappled with soft sun rays, gentle rustling leaves, and bird songs.";
    }

    @Override
    protected void populateCustomEntities(Map<String, Object> config, int intensity) {
        config.put("treeDensity", 12 + (intensity * 3));
        config.put("foliageColor", "#2e7d32");
        config.put("pathWidth", 3.0);
        config.put("particles", "falling_leaves");
    }

    @Override
    public int adjustIntensityForPatient(int currentIntensity, int stressDelta, int postStressRating) {
        if (postStressRating > 6) {
            return Math.max(1, currentIntensity - 1);
        }
        return currentIntensity;
    }
}
