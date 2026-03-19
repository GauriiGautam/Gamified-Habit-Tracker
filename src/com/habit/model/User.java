package com.habit.model;

import java.sql.Timestamp;

public class User {
    private long userId;
    private String username;
    private String email;
    private String password;
    private String fullName;
    private String bio;
    private String avatarUrl;
    private int currentLevel;
    private int totalXp;
    private Timestamp registrationDate;
    private Timestamp lastLoginDate;
    private boolean isActive;
    private String notificationPreferences; 
    private boolean aiPredictionEnabled;
    private boolean anomalyDetectionEnabled;
    private boolean adaptiveDifficultyEnabled;

    
    public User() {}

    public User(long userId, String username, String email, String password, String fullName, String bio, String avatarUrl, int currentLevel, int totalXp, Timestamp registrationDate, Timestamp lastLoginDate, boolean isActive, String notificationPreferences, boolean aiPredictionEnabled, boolean anomalyDetectionEnabled, boolean adaptiveDifficultyEnabled) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.bio = bio;
        this.avatarUrl = avatarUrl;
        this.currentLevel = currentLevel;
        this.totalXp = totalXp;
        this.registrationDate = registrationDate;
        this.lastLoginDate = lastLoginDate;
        this.isActive = isActive;
        this.notificationPreferences = notificationPreferences;
        this.aiPredictionEnabled = aiPredictionEnabled;
        this.anomalyDetectionEnabled = anomalyDetectionEnabled;
        this.adaptiveDifficultyEnabled = adaptiveDifficultyEnabled;
    }

   
    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public int getCurrentLevel() { return currentLevel; }
    public void setCurrentLevel(int currentLevel) { this.currentLevel = currentLevel; }

    public int getTotalXp() { return totalXp; }
    public void setTotalXp(int totalXp) { this.totalXp = totalXp; }

    public Timestamp getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(Timestamp registrationDate) { this.registrationDate = registrationDate; }

    public Timestamp getLastLoginDate() { return lastLoginDate; }
    public void setLastLoginDate(Timestamp lastLoginDate) { this.lastLoginDate = lastLoginDate; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String getNotificationPreferences() { return notificationPreferences; }
    public void setNotificationPreferences(String notificationPreferences) { this.notificationPreferences = notificationPreferences; }

    public boolean isAiPredictionEnabled() { return aiPredictionEnabled; }
    public void setAiPredictionEnabled(boolean aiPredictionEnabled) { this.aiPredictionEnabled = aiPredictionEnabled; }

    public boolean isAnomalyDetectionEnabled() { return anomalyDetectionEnabled; }
    public void setAnomalyDetectionEnabled(boolean anomalyDetectionEnabled) { this.anomalyDetectionEnabled = anomalyDetectionEnabled; }

    public boolean isAdaptiveDifficultyEnabled() { return adaptiveDifficultyEnabled; }
    public void setAdaptiveDifficultyEnabled(boolean adaptiveDifficultyEnabled) { this.adaptiveDifficultyEnabled = adaptiveDifficultyEnabled; }
}
