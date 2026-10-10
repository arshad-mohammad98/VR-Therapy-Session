package com.vrtherapy.service;

import com.vrtherapy.model.SystemSetting;
import java.util.List;
import java.util.Map;

/**
 * Service contract governing system-wide runtime configuration parameters.
 */
public interface SystemSettingService {

    /**
     * Gets setting value by key, or returns defaultValue if not found.
     */
    String getSettingValue(String key, String defaultValue);

    /**
     * Updates or creates a system setting.
     */
    SystemSetting setSetting(String key, String value, String description, String username);

    /**
     * Retrieves all system settings.
     */
    List<SystemSetting> getAllSettings();

    /**
     * Retrieves settings as a key-value Map.
     */
    Map<String, String> getSettingsMap();

    /**
     * Checks if maintenance mode is enabled.
     */
    boolean isMaintenanceModeEnabled();

    /**
     * Gets maximum allowed session duration in minutes.
     */
    int getMaxSessionDurationMinutes();
}
