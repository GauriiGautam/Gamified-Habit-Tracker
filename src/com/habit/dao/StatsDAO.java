package com.habit.dao;

import com.habit.db.DBConnection;
import com.habit.interfaces.ReportingService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StatsDAO implements ReportingService {

    private Connection conn;

    public StatsDAO() {
        try {
            this.conn = DBConnection.getConnection();
        } catch (SQLException e) {
            System.err.println("Failed to initialize StatsDAO: " + e.getMessage());
        }
    }

    public void viewHabitStats(int userId) {
        String sql = "SELECT h.HabitName, h.CurrentStreak, h.LongestStreak, "
                + "h.TotalCompletions, h.XPValue FROM HABIT h WHERE h.UserID = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                System.out.printf("%-22s %-10d %-12d %-12d %-10d%n",
                        rs.getString("HabitName"), rs.getInt("CurrentStreak"),
                        rs.getInt("LongestStreak"), rs.getInt("TotalCompletions"),
                        rs.getInt("XPValue"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching stats: " + e.getMessage());
        }
    }

    @Override
    public int getCurrentStreak(int userId, int habitId) {
        String sql = "SELECT CurrentStreak FROM HABIT WHERE UserID = ? AND HabitID = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, userId);
            pst.setInt(2, habitId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) return rs.getInt("CurrentStreak");
        } catch (SQLException e) {
            System.err.println("Error fetching current streak: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public int getLongestStreak(int userId, int habitId) {
        String sql = "SELECT LongestStreak FROM HABIT WHERE UserID = ? AND HabitID = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, userId);
            pst.setInt(2, habitId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) return rs.getInt("LongestStreak");
        } catch (SQLException e) {
            System.err.println("Error fetching longest streak: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public List<String> generateWeeklyReport(int userId) {
        // Dummy implementation for structural requirements
        return new ArrayList<>();
    }

    @Override
    public double calculateSuccessRate(int userId, int habitId) {
        // Dummy implementation for structural requirements
        return 0.0;
    }
}
