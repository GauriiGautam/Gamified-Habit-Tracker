package com.habit.model;

public class AdminUser extends User {

    private String adminCode;
    private String accessLevel;

    public AdminUser(int userId, String username, String email,
            String password, String fullName, String adminCode) {
        super(userId, username, email, password, fullName, "Admin Account", 5, 9999, "2024-01-01", 1);
        this.adminCode = adminCode;
        this.accessLevel = "FULL";
        this.currentLevel = 5;
    }

    public String getAdminCode() {
        return adminCode;
    }

    public String getAccessLevel() {
        return accessLevel;
    }

    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("Admin Code  : " + adminCode);
        System.out.println("Access Level: " + accessLevel);
    }

    public void viewSystemStats() {
        System.out.println("Admin " + getUsername() + " accessing system statistics...");
        System.out.println("Access Level: " + accessLevel);
    }
}