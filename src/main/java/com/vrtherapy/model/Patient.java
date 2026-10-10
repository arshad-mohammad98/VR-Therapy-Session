package com.vrtherapy.model;

/**
 * Domain entity representing an individual undergoing VR therapy protocols.
 *
 * Demonstrates:
 * - OOP Inheritance (extends abstract User)
 * - Polymorphism (overrides getRoleDescription())
 */
public class Patient extends User {

    private Long assignedTherapistId;
    private String emergencyContact;
    private Integer baselineAnxiety = 5;
    private String comfortSettingsJson;

    public Patient() {
        super();
        setRole(Role.PATIENT);
    }

    public Patient(String username, String password, String fullName, String email, boolean active) {
        super(username, password, fullName, email, Role.PATIENT, active);
    }

    @Override
    public String getRoleDescription() {
        return "Patient participant enrolled in VR desensitization and mindfulness therapy.";
    }

    public Long getAssignedTherapistId() {
        return assignedTherapistId;
    }

    public void setAssignedTherapistId(Long assignedTherapistId) {
        this.assignedTherapistId = assignedTherapistId;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public Integer getBaselineAnxiety() {
        return baselineAnxiety;
    }

    public void setBaselineAnxiety(Integer baselineAnxiety) {
        this.baselineAnxiety = baselineAnxiety;
    }

    public String getComfortSettingsJson() {
        return comfortSettingsJson;
    }

    public void setComfortSettingsJson(String comfortSettingsJson) {
        this.comfortSettingsJson = comfortSettingsJson;
    }
}
