package com.vrtherapy.model;

/**
 * Domain entity representing global platform runtime parameters.
 */
public class SystemSetting extends BaseEntity {

    private String settingKey;
    private String settingValue;
    private String description;

    public SystemSetting() {
        super();
    }

    public SystemSetting(String settingKey, String settingValue, String description) {
        super();
        this.settingKey = settingKey;
        this.settingValue = settingValue;
        this.description = description;
    }

    public String getSettingKey() {
        return settingKey;
    }

    public void setSettingKey(String settingKey) {
        this.settingKey = settingKey;
    }

    public String getSettingValue() {
        return settingValue;
    }

    public void setSettingValue(String settingValue) {
        this.settingValue = settingValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
