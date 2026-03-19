package com.habit.dao;

import com.habit.db.DatabaseConnectionManager;
import com.habit.model.HabitLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HabitLogDAO {

    /**
     * Retrieve all logs for a specific habit
     */
    public List<HabitLog> getLogsByHabitId(long habitId) {
        List<HabitLog> logs = new ArrayList<>();
        String query = "SELECT * FROM Habit_Log WHERE HabitID = ? ORDER BY CompletionDate DESC, CompletionTime DESC";

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setLong(1, habitId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(extractHabitLogFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logs;
    }

    /**
     * Insert a new Habit Log (Completion event)
     */
    public boolean logHabitCompletion(HabitLog log) {
        String query = "INSERT INTO Habit_Log (HabitID, UserID, CompletionDate, CompletionTime, Notes, XPAwarded, MoodAtCompletion, DifficultyAtCompletion) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setLong(1, log.getHabitId());
            stmt.setLong(2, log.getUserId());
            stmt.setDate(3, log.getCompletionDate());
            stmt.setTime(4, log.getCompletionTime());
            stmt.setString(5, log.getNotes());
            stmt.setInt(6, log.getXpAwarded());
            stmt.setObject(7, log.getMoodAtCompletion() == 0 ? null : log.getMoodAtCompletion(), Types.INTEGER);
            stmt.setFloat(8, log.getDifficultyAtCompletion() == 0 ? 1.0f : log.getDifficultyAtCompletion());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private HabitLog extractHabitLogFromResultSet(ResultSet rs) throws SQLException {
        return new HabitLog(
                rs.getLong("LogID"),
                rs.getLong("HabitID"),
                rs.getLong("UserID"),
                rs.getDate("CompletionDate"),
                rs.getTime("CompletionTime"),
                rs.getString("Notes"),
                rs.getInt("XPAwarded"),
                rs.getInt("MoodAtCompletion"), // will return 0 if null, but we check if null before setting typically
                rs.getFloat("DifficultyAtCompletion"),
                rs.getBoolean("IsEdited"),
                rs.getTimestamp("CreatedTimestamp")
        );
    }
}
