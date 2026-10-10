package com.vrtherapy.model;

/**
 * Domain entity representing licensed clinicians managing therapy plans and live VR sessions.
 *
 * Demonstrates:
 * - OOP Inheritance (extends abstract User)
 * - Polymorphism (overrides getRoleDescription())
 */
public class Therapist extends User {

    private String licenseNumber;
    private String specialization;
    private Integer maxPatients = 30;

    public Therapist() {
        super();
        setRole(Role.THERAPIST);
    }

    public Therapist(String username, String password, String fullName, String email, boolean active) {
        super(username, password, fullName, email, Role.THERAPIST, active);
    }

    @Override
    public String getRoleDescription() {
        return "Licensed clinical therapist designing protocols and administering live VR sessions.";
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

    public Integer getMaxPatients() {
        return maxPatients;
    }

    public void setMaxPatients(Integer maxPatients) {
        this.maxPatients = maxPatients;
    }
}
