package com.habit.dao;

import com.habit.db.DatabaseConnectionManager;
import com.habit.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public User authenticate(String username, String password) {
        String query = "SELECT * FROM User WHERE Username = ? AND Password = ?";
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password); 

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractUserFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; 
    }

    
    public User getUserById(long userId) {
        String query = "SELECT * FROM User WHERE UserID = ?";
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractUserFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    
    public boolean registerUser(User user) {
        String query = "INSERT INTO User (Username, Email, Password, FullName, CurrentLevel, TotalXP, IsActive, AIPredictionEnabled, AnomalyDetectionEnabled, AdaptiveDifficultyEnabled) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getFullName());
            stmt.setInt(5, user.getCurrentLevel() == 0 ? 1 : user.getCurrentLevel());
            stmt.setInt(6, user.getTotalXp());
            stmt.setBoolean(7, user.isActive());
            stmt.setBoolean(8, user.isAiPredictionEnabled());
            stmt.setBoolean(9, user.isAnomalyDetectionEnabled());
            stmt.setBoolean(10, user.isAdaptiveDifficultyEnabled());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    
    public boolean updateLastLogin(long userId) {
        String query = "UPDATE User SET LastLoginDate = CURRENT_TIMESTAMP WHERE UserID = ?";
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    
    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        return new User(
                rs.getLong("UserID"),
                rs.getString("Username"),
                rs.getString("Email"),
                rs.getString("Password"),
                rs.getString("FullName"),
                rs.getString("Bio"),
                rs.getString("AvatarURL"),
                rs.getInt("CurrentLevel"),
                rs.getInt("TotalXP"),
                rs.getTimestamp("RegistrationDate"),
                rs.getTimestamp("LastLoginDate"),
                rs.getBoolean("IsActive"),
                rs.getString("NotificationPreferences"),
                rs.getBoolean("AIPredictionEnabled"),
                rs.getBoolean("AnomalyDetectionEnabled"),
                rs.getBoolean("AdaptiveDifficultyEnabled")
        );
    }
}
