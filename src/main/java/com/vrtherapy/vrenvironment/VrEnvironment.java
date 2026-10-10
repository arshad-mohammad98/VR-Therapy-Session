package com.vrtherapy.vrenvironment;

import com.vrtherapy.model.EnvironmentType;
import java.util.Map;

/**
 * Interface defining polymorphic VR 3D environment specifications and behavioral adjustments.
 * Demonstrates OOP Polymorphism and Strategy/Factory integration.
 */
public interface VrEnvironment {

    /**
     * @return The canonical EnvironmentType enumeration.
     */
    EnvironmentType getType();

    /**
     * @return Human-readable title of the virtual environment.
     */
    String getName();

    /**
     * Calculates sky ambient hex color based on the chosen therapy intensity.
     */
    String getSkyColor(int intensity);

    /**
     * Ground or terrain base coloration.
     */
    String getGroundColor();

    /**
     * Web-safe procedural sound or synthesizer generator key.
     */
    String getAmbientSoundAsset();

    /**
     * Generates complete JSON/Map configuration consumed by the WebXR A-Frame frontend engine.
     */
    Map<String, Object> generateSceneConfiguration(int intensity, boolean guidedBreathing, boolean backgroundAudio);

    /**
     * Dynamically adjusts environment intensity based on biometric or patient feedback.
     */
    int adjustIntensityForPatient(int currentIntensity, int stressDelta, int postStressRating);

    /**
     * Description of psychological affordances provided by this environment.
     */
    String getAtmosphereDescription();
}
