package com.vrtherapy.service.impl;

import com.vrtherapy.dao.SettingDao;
import com.vrtherapy.dao.impl.SettingDaoImpl;
import com.vrtherapy.model.SystemSetting;
import com.vrtherapy.service.SystemSettingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SystemSettingServiceImpl implements SystemSettingService {

    private static final Logger logger = LoggerFactory.getLogger(SystemSettingServiceImpl.class);
    private final SettingDao settingDao;

    public SystemSettingServiceImpl() {
        this(new SettingDaoImpl());
    }

    public SystemSettingServiceImpl(SettingDao settingDao) {
        this.settingDao = settingDao;
    }

    @Override
    public String getSettingValue(String key, String defaultValue) {
        return settingDao.getSettingValue(key, defaultValue);
    }

    @Override
    public SystemSetting setSetting(String key, String value, String description, String username) {
        settingDao.updateValue(key, value);
        logger.info("Setting '{}' updated to '{}' by {}", key, value, username);
        return settingDao.findByKey(key).orElseGet(() -> new SystemSetting(key, value, description));
    }

    @Override
    public List<SystemSetting> getAllSettings() {
        return settingDao.findAll();
    }

    @Override
    public Map<String, String> getSettingsMap() {
        Map<String, String> map = new HashMap<>();
        for (SystemSetting s : getAllSettings()) {
            map.put(s.getSettingKey(), s.getSettingValue());
        }
        return map;
    }

    @Override
    public boolean isMaintenanceModeEnabled() {
        return Boolean.parseBoolean(getSettingValue("maintenance_mode", "false"));
    }

    @Override
    public int getMaxSessionDurationMinutes() {
        try {
            return Integer.parseInt(getSettingValue("max_session_duration_minutes", "45"));
        } catch (NumberFormatException e) {
            return 45;
        }
    }
}
