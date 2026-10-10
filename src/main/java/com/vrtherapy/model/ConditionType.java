package com.vrtherapy.model;

/**
 * Mental health and rehabilitation condition types supported by VR therapy protocols.
 */
public enum ConditionType {
    ANXIETY("Generalized Anxiety", "Techniques focused on autonomic regulation and grounding."),
    PTSD("Post-Traumatic Stress Disorder", "Gradual exposure and trauma-informed relaxation."),
    PHOBIA("Specific Phobias", "Systematic desensitization and graded virtual exposure."),
    DEPRESSION("Depression & Low Mood", "Behavioral activation through uplifting sensory environments."),
    STRESS("Occupational & Chronic Stress", "Mindfulness-based stress reduction and deep breathing."),
    INSOMNIA("Insomnia & Sleep Disturbance", "Somnology decompression and parasympathetic sleep induction.");

    private final String displayName;
    private final String clinicalFocus;

    ConditionType(String displayName, String clinicalFocus) {
        this.displayName = displayName;
        this.clinicalFocus = clinicalFocus;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getClinicalFocus() {
        return clinicalFocus;
    }
}
