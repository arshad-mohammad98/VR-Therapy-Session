package com.vrtherapy.dao;

import com.vrtherapy.model.SystemSetting;

import java.util.Optional;

public interface SettingDao extends GenericDao<SystemSetting, Long> {

    Optional<SystemSetting> findByKey(String settingKey);

    void updateValue(String settingKey, String settingValue);

    String getSettingValue(String key, String defaultValue);
}
