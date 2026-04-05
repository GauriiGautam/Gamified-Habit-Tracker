package com.habit.model;

public class Habit {

    private int habitId;
    private int userId;
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
    private String createdDate;

    public Habit() {
    }

    public Habit(int habitId, int userId, int categoryId, String habitName,
            String description, String frequency, int targetCount,
            int difficultyLevel, int xpValue, int currentStreak,
            int longestStreak, int totalCompletions,
            String isActive, String createdDate) {
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
    }

    public int getHabitId() {
        return habitId;
    }

    public void setHabitId(int habitId) {
        this.habitId = habitId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getHabitName() {
        return habitName;
    }

    public void setHabitName(String habitName) {
        this.habitName = habitName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public int getTargetCount() {
        return targetCount;
    }

    public void setTargetCount(int targetCount) {
        this.targetCount = targetCount;
    }

    public int getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(int difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public int getXpValue() {
        return xpValue;
    }

    public void setXpValue(int xpValue) {
        this.xpValue = xpValue;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
    }

    public int getLongestStreak() {
        return longestStreak;
    }

    public void setLongestStreak(int longestStreak) {
        this.longestStreak = longestStreak;
    }

    public int getTotalCompletions() {
        return totalCompletions;
    }

    public void setTotalCompletions(int totalCompletions) {
        this.totalCompletions = totalCompletions;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return "Habit[" + habitId + " | " + habitName + " | Streak: " + currentStreak + "]";
    }
}