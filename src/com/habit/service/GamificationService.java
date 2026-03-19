package com.habit.service;

import com.habit.dao.UserDAO;
import com.habit.model.User;
import com.habit.db.DatabaseConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GamificationService {
    private UserDAO userDAO;

    public GamificationService() {
        this.userDAO = new UserDAO();
    }

    /**
     * Call this when a habit is completed to award XP and potentially level up the user.
     */
    public void awardXP(long userId, int xpAmount) {
        User user = userDAO.getUserById(userId);
        if (user == null) return;

        int newTotalXp = user.getTotalXp() + xpAmount;
        user.setTotalXp(newTotalXp);

        // Check for level up
        int nextLevel = user.getCurrentLevel() + 1;
        if (hasReachedNextLevel(newTotalXp, nextLevel)) {
            user.setCurrentLevel(nextLevel);
            System.out.println("Congratulations! User " + user.getUsername() + " leveled up to Level " + nextLevel + "!");
            // In a real scenario, we might trigger a UI notification event here
        }

        // Save back to DB
        saveUserGamificationState(user);
    }

    /**
     * Checks DB to see if XP required for next level has been met
     */
    private boolean hasReachedNextLevel(int currentXp, int nextLevel) {
        String query = "SELECT XPRequired FROM Level WHERE LevelNumber = ?";
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, nextLevel);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int xpRequired = rs.getInt("XPRequired");
                    return currentXp >= xpRequired;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Level doesn't exist or error
    }

    /**
     * Saves user XP and Level to database
     */
    private void saveUserGamificationState(User user) {
        String query = "UPDATE User SET TotalXP = ?, CurrentLevel = ? WHERE UserID = ?";
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, user.getTotalXp());
            stmt.setInt(2, user.getCurrentLevel());
            stmt.setLong(3, user.getUserId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Additional methods for unlocking badges, computing difficulty multipliers, etc. would go here.
}
