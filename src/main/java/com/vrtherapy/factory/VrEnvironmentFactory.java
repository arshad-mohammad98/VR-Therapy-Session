package com.vrtherapy.factory;

import com.vrtherapy.model.EnvironmentType;
import com.vrtherapy.vrenvironment.BeachEnvironment;
import com.vrtherapy.vrenvironment.BreathingRoomEnvironment;
import com.vrtherapy.vrenvironment.ForestEnvironment;
import com.vrtherapy.vrenvironment.HeightsEnvironment;
import com.vrtherapy.vrenvironment.NightSkyEnvironment;
import com.vrtherapy.vrenvironment.SocialEnvironment;
import com.vrtherapy.vrenvironment.VrEnvironment;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 * Factory pattern implementation providing centralized discovery and instantiation
 * of polymorphic VrEnvironment implementations.
 *
 * Demonstrates:
 * - Factory Design Pattern
 * - Polymorphism (VrEnvironment interface implementations)
 * - Thread-safe, immutable enum-based map lookup
 */
public class VrEnvironmentFactory {

    private static final Map<EnvironmentType, VrEnvironment> REGISTRY = new EnumMap<>(EnvironmentType.class);

    static {
        register(new BeachEnvironment());
        register(new ForestEnvironment());
        register(new HeightsEnvironment());
        register(new SocialEnvironment());
        register(new BreathingRoomEnvironment());
        register(new NightSkyEnvironment());
    }

    private static void register(VrEnvironment env) {
        REGISTRY.put(env.getType(), env);
    }

    /**
     * Retrieves the concrete polymorphic VrEnvironment instance corresponding to the given EnvironmentType enum.
     *
     * @param type EnvironmentType enum key
     * @return matching VrEnvironment instance
     */
    public static VrEnvironment getEnvironment(EnvironmentType type) {
        VrEnvironment env = REGISTRY.get(type);
        if (env == null) {
            // Default fallback
            return REGISTRY.get(EnvironmentType.CALM_BEACH);
        }
        return env;
    }

    /**
     * Returns an unmodifiable map of all registered VR environments.
     */
    public static Map<EnvironmentType, VrEnvironment> getAllEnvironments() {
        return Collections.unmodifiableMap(REGISTRY);
    }
}
