package com.vrtherapy.model;

/**
 * User roles defining authorization privileges in the system.
 */
public enum Role {
    ADMIN("System Administrator"),
    THERAPIST("Licensed Therapist"),
    PATIENT("Registered Patient"),
    ROLE_ADMIN("System Administrator"),
    ROLE_THERAPIST("Licensed Therapist"),
    ROLE_PATIENT("Registered Patient");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Role fromString(String str) {
        if (str == null) return PATIENT;
        String clean = str.trim().toUpperCase();
        try {
            return Role.valueOf(clean);
        } catch (IllegalArgumentException e) {
            if (clean.startsWith("ROLE_")) {
                try {
                    return Role.valueOf(clean.substring(5));
                } catch (IllegalArgumentException ex) {
                    return PATIENT;
                }
            }
            return PATIENT;
        }
    }
}
