package com.habit.model;

public class User {

    private int userId;
    private String username;
    private String email;
    private String password;
    private String fullName;
    private String bio;
    protected int currentLevel;
    private int totalXP;
    private String registrationDate;
    private int isActive;

    public User() {
    }

    public User(int userId, String username, String email, String password,
            String fullName, String bio, int currentLevel,
            int totalXP, String registrationDate, int isActive) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.bio = bio;
        this.currentLevel = currentLevel;
        this.totalXP = totalXP;
        this.registrationDate = registrationDate;
        this.isActive = isActive;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public int getTotalXP() {
        return totalXP;
    }

    public void setTotalXP(int totalXP) {
        this.totalXP = totalXP;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public void displayInfo() {
        System.out.println("User ID    : " + userId);
        System.out.println("Username   : " + username);
        System.out.println("Email      : " + email);
        System.out.println("Full Name  : " + fullName);
        System.out.println("Total XP   : " + totalXP);
        System.out.println("Level      : " + currentLevel);
    }

    @Override
    public String toString() {
        return "User[" + userId + " | " + username + " | XP: " + totalXP + "]";
    }
}