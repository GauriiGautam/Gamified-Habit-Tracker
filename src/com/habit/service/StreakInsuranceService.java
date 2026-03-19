package com.habit.service;

import com.habit.db.DatabaseConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class StreakInsuranceService {

   
    public boolean grantToken(long userId, long habitId, String source) {
        
        String query = "INSERT INTO Streak_Insurance (UserID, HabitID, TotalTokens, UsedTokens, EarnedSource) " +
                       "VALUES (?, ?, 1, 0, ?) ON DUPLICATE KEY UPDATE TotalTokens = TotalTokens + 1, EarnedSource = ?";
        
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setLong(1, userId);
            stmt.setLong(2, habitId);
            stmt.setString(3, source);
            stmt.setString(4, source);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

     boolean useToken(long userId, long habitId) {
        
        String checkQuery = "SELECT TotalTokens, UsedTokens FROM Streak_Insurance WHERE UserID = ? AND HabitID = ?";
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
            
            checkStmt.setLong(1, userId);
            checkStmt.setLong(2, habitId);
            
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    int total = rs.getInt("TotalTokens");
                    int used = rs.getInt("UsedTokens");

                    if (total > used) {
                        
                        String updateQuery = "UPDATE Streak_Insurance SET UsedTokens = UsedTokens + 1, LastUsedDate = CURRENT_TIMESTAMP WHERE UserID = ? AND HabitID = ?";
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                            updateStmt.setLong(1, userId);
                            updateStmt.setLong(2, habitId);
                            updateStmt.executeUpdate();
                            return true; 
                        }
                    } else {
                        System.out.println("Insufficient streak insurance tokens available.");
                        return false;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
