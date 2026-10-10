package com.vrtherapy.model;

/**
 * Domain entity representing system administrators overseeing users, hardware, and security.
 *
 * Demonstrates:
 * - OOP Inheritance (extends abstract User)
 * - Polymorphism (overrides getRoleDescription())
 */
public class Admin extends User {

    private String department = "System Administration";
    private Integer accessLevel = 1;

    public Admin() {
        super();
        setRole(Role.ADMIN);
    }

    public Admin(String username, String password, String fullName, String email, boolean active) {
        super(username, password, fullName, email, Role.ADMIN, active);
    }

    @Override
    public String getRoleDescription() {
        return "System administrator managing devices, security settings, audit logs, and support.";
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Integer getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(Integer accessLevel) {
        this.accessLevel = accessLevel;
    }
}
