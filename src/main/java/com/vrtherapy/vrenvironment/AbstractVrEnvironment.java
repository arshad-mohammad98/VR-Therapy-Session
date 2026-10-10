package com.vrtherapy.vrenvironment;

import com.vrtherapy.model.EnvironmentType;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract skeletal implementation of VrEnvironment providing shared configuration builders.
 */
public abstract class AbstractVrEnvironment implements VrEnvironment {

    private final EnvironmentType type;
    private final String name;

    protected AbstractVrEnvironment(EnvironmentType type, String name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public EnvironmentType getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Map<String, Object> generateSceneConfiguration(int intensity, boolean guidedBreathing, boolean backgroundAudio) {
        int clampedIntensity = Math.max(1, Math.min(5, intensity));
        Map<String, Object> config = new HashMap<>();
        config.put("environmentType", type.name());
        config.put("name", name);
        config.put("intensity", clampedIntensity);
        config.put("skyColor", getSkyColor(clampedIntensity));
        config.put("groundColor", getGroundColor());
        config.put("ambientSound", getAmbientSoundAsset());
        config.put("backgroundAudioEnabled", backgroundAudio);
        config.put("guidedBreathingEnabled", guidedBreathing);
        config.put("fogDensity", calculateFogDensity(clampedIntensity));
        config.put("lightIntensity", calculateLightIntensity(clampedIntensity));
        config.put("atmosphereDescription", getAtmosphereDescription());

        // Breathing sphere parameters
        Map<String, Object> breathingConfig = new HashMap<>();
        breathingConfig.put("inhalationSeconds", 4);
        breathingConfig.put("holdSeconds", 4);
        breathingConfig.put("exhalationSeconds", 4);
        breathingConfig.put("minRadius", 0.3);
        breathingConfig.put("maxRadius", 0.9);
        breathingConfig.put("glowColor", "#64ffda");
        config.put("breathingGuide", breathingConfig);

        populateCustomEntities(config, clampedIntensity);
        return config;
    }

    /**
     * Hook method allowing subclasses to inject specialized 3D geometries and objects.
     */
    protected abstract void populateCustomEntities(Map<String, Object> config, int intensity);

    protected double calculateFogDensity(int intensity) {
        return 0.01 + (intensity * 0.005);
    }

    protected double calculateLightIntensity(int intensity) {
        return 0.6 + (intensity * 0.08);
    }
}
