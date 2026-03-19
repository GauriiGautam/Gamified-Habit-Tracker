package com.habit.model;

import java.sql.Time;
import java.sql.Timestamp;

public class Habit {
    private long habitId;
    private long userId;
    private int categoryId;
    private String habitName;
    private String description;
    private String frequency;
    private int targetCount;
    private int difficultyLevel;
    private int xpValue;
    private int currentStreak;
    private int longestStreak;
    private int totalCompletions;
    private String isActive;
    private Timestamp createdDate;
    private Time reminderTime;
    private String iconName;
    private boolean adaptiveDifficultyEnabled;
    private float currentDifficultyMultiplier;
    private float optimalDifficultyScore;

    public Habit() {}

    public Habit(long habitId, long userId, int categoryId, String habitName, String description, String frequency, int targetCount, int difficultyLevel, int xpValue, int currentStreak, int longestStreak, int totalCompletions, String isActive, Timestamp createdDate, Time reminderTime, String iconName, boolean adaptiveDifficultyEnabled, float currentDifficultyMultiplier, float optimalDifficultyScore) {
        this.habitId = habitId;
        this.userId = userId;
        this.categoryId = categoryId;
        this.habitName = habitName;
        this.description = description;
        this.frequency = frequency;
        this.targetCount = targetCount;
        this.difficultyLevel = difficultyLevel;
        this.xpValue = xpValue;
        this.currentStreak = currentStreak;
        this.longestStreak = longestStreak;
        this.totalCompletions = totalCompletions;
        this.isActive = isActive;
        this.createdDate = createdDate;
        this.reminderTime = reminderTime;
        this.iconName = iconName;
        this.adaptiveDifficultyEnabled = adaptiveDifficultyEnabled;
        this.currentDifficultyMultiplier = currentDifficultyMultiplier;
        this.optimalDifficultyScore = optimalDifficultyScore;
    }

  
    public long getHabitId() { return habitId; }
    public void setHabitId(long habitId) { this.habitId = habitId; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getHabitName() { return habitName; }
    public void setHabitName(String habitName) { this.habitName = habitName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }

    public int getTargetCount() { return targetCount; }
    public void setTargetCount(int targetCount) { this.targetCount = targetCount; }

    public int getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(int difficultyLevel) { this.difficultyLevel = difficultyLevel; }

    public int getXpValue() { return xpValue; }
    public void setXpValue(int xpValue) { this.xpValue = xpValue; }

    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }

    public int getLongestStreak() { return longestStreak; }
    public void setLongestStreak(int longestStreak) { this.longestStreak = longestStreak; }

    public int getTotalCompletions() { return totalCompletions; }
    public void setTotalCompletions(int totalCompletions) { this.totalCompletions = totalCompletions; }

    public String getIsActive() { return isActive; }
    public void setIsActive(String isActive) { this.isActive = isActive; }

    public Timestamp getCreatedDate() { return createdDate; }
    public void setCreatedDate(Timestamp createdDate) { this.createdDate = createdDate; }

    public Time getReminderTime() { return reminderTime; }
    public void setReminderTime(Time reminderTime) { this.reminderTime = reminderTime; }

    public String getIconName() { return iconName; }
    public void setIconName(String iconName) { this.iconName = iconName; }

    public boolean isAdaptiveDifficultyEnabled() { return adaptiveDifficultyEnabled; }
    public void setAdaptiveDifficultyEnabled(boolean adaptiveDifficultyEnabled) { this.adaptiveDifficultyEnabled = adaptiveDifficultyEnabled; }

    public float getCurrentDifficultyMultiplier() { return currentDifficultyMultiplier; }
    public void setCurrentDifficultyMultiplier(float currentDifficultyMultiplier) { this.currentDifficultyMultiplier = currentDifficultyMultiplier; }

    public float getOptimalDifficultyScore() { return optimalDifficultyScore; }
    public void setOptimalDifficultyScore(float optimalDifficultyScore) { this.optimalDifficultyScore = optimalDifficultyScore; }
}
