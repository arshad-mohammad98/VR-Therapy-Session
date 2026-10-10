package com.vrtherapy.model;

/**
 * Abstract domain model representing authenticated actors in the VR Therapy Platform.
 *
 * Demonstrates:
 * - OOP Abstraction and Polymorphism (Specialized by Patient, Therapist, and Admin)
 * - Encapsulation with explicit getters and setters
 */
public abstract class User extends BaseEntity {

    private String username;
    private String password;
    private String fullName;
    private String email;
    private Role role;
    private boolean active = true;
    private String phone;
    private String clinicalBioOrNotes;

    public User() {
        super();
    }

    public User(String username, String password, String fullName, String email, Role role, boolean active) {
        super();
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.active = active;
    }

    /**
     * Polymorphic method allowing each user role to supply its domain dashboard title or summary.
     */
    public abstract String getRoleDescription();

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

    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", active=" + active +
                '}';
    }
}
