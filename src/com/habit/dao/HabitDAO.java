package com.habit.dao;

import com.habit.db.DatabaseConnectionManager;
import com.habit.model.Habit;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HabitDAO {

    public List<Habit> getHabitsByUserId(long userId) {
        List<Habit> habits = new ArrayList<>();
        String query = "SELECT * FROM Habit WHERE UserID = ? AND IsActive = 'ACTIVE'";

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    habits.add(extractHabitFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return habits;
    }

    
    public boolean createHabit(Habit habit) {
        String query = "INSERT INTO Habit (UserID, CategoryID, HabitName, Description, Frequency, TargetCount, DifficultyLevel, XPValue, IconName, ReminderTime) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setLong(1, habit.getUserId());
            stmt.setInt(2, habit.getCategoryId());
            stmt.setString(3, habit.getHabitName());
            stmt.setString(4, habit.getDescription());
            stmt.setString(5, habit.getFrequency());
            stmt.setInt(6, habit.getTargetCount());
            stmt.setInt(7, habit.getDifficultyLevel() > 0 ? habit.getDifficultyLevel() : 2); // default Medium
            stmt.setInt(8, habit.getXpValue() > 0 ? habit.getXpValue() : 10);
            stmt.setString(9, habit.getIconName());
            stmt.setTime(10, habit.getReminderTime());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    
    public boolean updateHabitProgress(long habitId, int newCurrentStreak, int newLongestStreak, int newTotalCompletions) {
        String query = "UPDATE Habit SET CurrentStreak = ?, LongestStreak = ?, TotalCompletions = ? WHERE HabitID = ?";
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, newCurrentStreak);
            stmt.setInt(2, newLongestStreak);
            stmt.setInt(3, newTotalCompletions);
            stmt.setLong(4, habitId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    
    public boolean archiveHabit(long habitId) {
        String query = "UPDATE Habit SET IsActive = 'ARCHIVED' WHERE HabitID = ?";
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setLong(1, habitId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    
    private Habit extractHabitFromResultSet(ResultSet rs) throws SQLException {
        return new Habit(
                rs.getLong("HabitID"),
                rs.getLong("UserID"),
                rs.getInt("CategoryID"),
                rs.getString("HabitName"),
                rs.getString("Description"),
                rs.getString("Frequency"),
                rs.getInt("TargetCount"),
                rs.getInt("DifficultyLevel"),
                rs.getInt("XPValue"),
                rs.getInt("CurrentStreak"),
                rs.getInt("LongestStreak"),
                rs.getInt("TotalCompletions"),
                rs.getString("IsActive"),
                rs.getTimestamp("CreatedDate"),
                rs.getTime("ReminderTime"),
                rs.getString("IconName"),
                rs.getBoolean("AdaptiveDifficultyEnabled"),
                rs.getFloat("CurrentDifficultyMultiplier"),
                rs.getFloat("OptimalDifficultyScore")
        );
    }
}
