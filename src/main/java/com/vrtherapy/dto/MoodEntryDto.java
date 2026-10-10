package com.vrtherapy.dto;


public class MoodEntryDto {

    private int moodScore = 5;

    private int energyScore = 5;

    private String emotionTags;
    private String notes;

    public MoodEntryDto() {
    }

    public int getMoodScore() {
        return moodScore;
    }

    public void setMoodScore(int moodScore) {
        this.moodScore = moodScore;
    }

    public int getEnergyScore() {
        return energyScore;
    }

    public void setEnergyScore(int energyScore) {
        this.energyScore = energyScore;
    }

    public String getEmotionTags() {
        return emotionTags;
    }

    public void setEmotionTags(String emotionTags) {
        this.emotionTags = emotionTags;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
