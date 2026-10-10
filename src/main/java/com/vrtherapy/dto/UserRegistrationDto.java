package com.vrtherapy.dto;

import com.vrtherapy.model.Role;

/**
 * Data transfer object for user account creation and updates.
 */
public class UserRegistrationDto {

    private Long id;

    private String username;

    private String password;

    private String fullName;

    private String email;

    private Role role;

    private boolean active = true;

    private String phone;

    private String clinicalBioOrNotes;

    public UserRegistrationDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getClinicalBioOrNotes() {
        return clinicalBioOrNotes;
    }

    public void setClinicalBioOrNotes(String clinicalBioOrNotes) {
        this.clinicalBioOrNotes = clinicalBioOrNotes;
    }

    private Long assignedTherapistId;
    private String emergencyContact;
    private Integer baselineAnxiety;
    private String licenseNumber;
    private String specialization;
    private String department;

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

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
