package com.habit.dao;

import com.habit.db.DatabaseConnectionManager;
import com.habit.model.Goal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GoalDAO {

   
    public List<Goal> getActiveGoalsForHabit(long habitId) {
        List<Goal> goals = new ArrayList<>();
        String query = "SELECT * FROM Goal WHERE HabitID = ? AND Status = 'IN_PROGRESS'";

        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setLong(1, habitId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    goals.add(extractGoalFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return goals;
    }

  
    public boolean createGoal(Goal goal) {
        String query = "INSERT INTO Goal (HabitID, GoalType, TargetCompletions, StartDate, EndDate, Status) " +
                       "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setLong(1, goal.getHabitId());
            stmt.setString(2, goal.getGoalType());
            stmt.setInt(3, goal.getTargetCompletions());
            stmt.setDate(4, goal.getStartDate());
            stmt.setDate(5, goal.getEndDate());
            stmt.setString(6, "IN_PROGRESS");

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    
    public boolean updateGoalProgress(long goalId, int newCompletedCount, String newStatus) {
        String query = "UPDATE Goal SET CompletedCount = ?, Status = ? WHERE GoalID = ?";
        try (Connection conn = DatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, newCompletedCount);
            stmt.setString(2, newStatus);
            stmt.setLong(3, goalId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Goal extractGoalFromResultSet(ResultSet rs) throws SQLException {
        return new Goal(
                rs.getLong("GoalID"),
                rs.getLong("HabitID"),
                rs.getString("GoalType"),
                rs.getInt("TargetCompletions"),
                rs.getDate("StartDate"),
                rs.getDate("EndDate"),
                rs.getString("Status"),
                rs.getInt("CompletedCount"),
                rs.getTimestamp("CreatedDate")
        );
    }
}
