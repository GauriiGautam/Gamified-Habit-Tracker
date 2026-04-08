package com.habit.dao;

import com.habit.db.DBConnection;
import com.habit.exceptions.HabitNotFoundException;
import com.habit.interfaces.HabitOperations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class HabitDAO implements HabitOperations {

    private Connection conn;
    private GamificationDAO gamificationDAO;

    public HabitDAO() {
        try {
            this.conn = DBConnection.getConnection();
            this.gamificationDAO = new GamificationDAO();
        } catch (SQLException e) {
            System.err.println("Failed to initialize DAO: " + e.getMessage());
        }
    }

    @Override
    public void addHabit(int userId, int categoryId, String habitName,
            String frequency, int difficultyLevel, int xpValue) {
        String sql = "INSERT INTO HABIT (HabitID, UserID, CategoryID, HabitName, Frequency, DifficultyLevel, XPValue, CreatedDate, IsActive) "
                + "VALUES ((SELECT IFNULL(MAX(HabitID),0)+1 FROM HABIT h2), ?, ?, ?, ?, ?, ?, CURDATE(), 'Active')";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, userId);
            pst.setInt(2, categoryId);
            pst.setString(3, habitName);
            pst.setString(4, frequency);
            pst.setInt(5, difficultyLevel);
            pst.setInt(6, xpValue);
            pst.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding habit: " + e.getMessage());
        }
    }

    @Override
    public void updateHabit(int habitId, String newName) {
        String sql = "UPDATE HABIT SET HabitName = ? WHERE HabitID = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, newName);
            pst.setInt(2, habitId);
            int rows = pst.executeUpdate();
            if (rows == 0) {
                 System.err.println("Habit not found");
            }
        } catch (SQLException e) {
            System.err.println("Error updating habit: " + e.getMessage());
        }
    }

    @Override
    public void deleteHabit(int habitId) {

        String[] cleanupSqls = {
                "DELETE FROM NOTIFICATION WHERE HabitID = ?",
                "DELETE FROM XP_TRANSACTION WHERE HabitID = ?",
                "DELETE FROM HABIT_SHARING WHERE HabitID = ?",
                "DELETE FROM ADAPTIVE_DIFFICULTY_LOG WHERE HabitID = ?",
                "DELETE FROM HABIT_PREDICTION WHERE HabitID = ?",
                "DELETE FROM STREAK_INSURANCE WHERE HabitID = ?",
                "DELETE FROM GOAL WHERE HabitID = ?",
                "DELETE FROM HABIT_LOG WHERE HabitID = ?"
        };
        try {
            for (String sql : cleanupSqls) {
                try (PreparedStatement pst = conn.prepareStatement(sql)) {
                    pst.setInt(1, habitId);
                    pst.executeUpdate();
                }
            }

            try (PreparedStatement pst = conn.prepareStatement("DELETE FROM HABIT WHERE HabitID = ?")) {
                pst.setInt(1, habitId);
                int rows = pst.executeUpdate();
                if(rows == 0) {
                    System.err.println("Habit not found");
                } else {
                    String reseq = "UPDATE HABIT SET HabitID = HabitID - 1 WHERE HabitID > ?";
                    try (PreparedStatement p4 = conn.prepareStatement(reseq)) {
                        p4.setInt(1, habitId);
                        p4.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error deleting habit: " + e.getMessage());
        }
    }

    @Override
    public void updateFullHabit(int habitId, String name, String category, String frequency, int target, int difficulty) {
        String getCatId = "SELECT CategoryID FROM CATEGORY WHERE CategoryName = ?";
        int catId = -1;
        try (PreparedStatement ps = conn.prepareStatement(getCatId)) {
            ps.setString(1, category);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) catId = rs.getInt("CategoryID");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        if (catId == -1) return;

        int xpVal = difficulty * 10;
        String sql = "UPDATE HABIT SET HabitName=?, CategoryID=?, Frequency=?, TargetCount=?, DifficultyLevel=?, XPValue=? WHERE HabitID=?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, name);
            pst.setInt(2, catId);
            pst.setString(3, frequency);
            pst.setInt(4, target);
            pst.setInt(5, difficulty);
            pst.setInt(6, xpVal);
            pst.setInt(7, habitId);
            pst.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating habit: " + e.getMessage());
        }
    }

    public void updateHabitStatus(int habitId, String status) throws HabitNotFoundException {
        String sql = "UPDATE HABIT SET IsActive = ? WHERE HabitID = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, status);
            pst.setInt(2, habitId);
            int rows = pst.executeUpdate();
            if(rows == 0) throw new HabitNotFoundException("Habit ID " + habitId + " not found.");
        } catch (SQLException e) {
            System.err.println("Error updating habit status: " + e.getMessage());
        }
    }

    @Override
    public List<String> logHabit(int habitId, int userId, String completionDate, String notes) {
        int xpValue = 10;
        try (PreparedStatement pst = conn.prepareStatement(
                "SELECT XPValue FROM HABIT WHERE HabitID = ?")) {
            pst.setInt(1, habitId);
            ResultSet rs = pst.executeQuery();
            if (rs.next())
                xpValue = rs.getInt("XPValue");
        } catch (SQLException e) {
            System.err.println("Warning: could not read habit XP: " + e.getMessage());
        }

        String insertLog = "INSERT INTO HABIT_LOG (LogID, HabitID, UserID, CompletionDate, CompletionTime, Notes, XPAwarded) "
                + "VALUES ((SELECT IFNULL(MAX(LogID),0)+1 FROM HABIT_LOG h2), ?, ?, ?, CURTIME(), ?, ?)";
        try (PreparedStatement pst = conn.prepareStatement(insertLog)) {
            pst.setInt(1, habitId);
            pst.setInt(2, userId);
            pst.setString(3, completionDate);
            pst.setString(4, notes);
            pst.setInt(5, xpValue);
            int rows = pst.executeUpdate();
            if (rows == 0)
                return new ArrayList<>();
        } catch (SQLException e) {
            System.err.println("Error logging habit: " + e.getMessage());
            return new ArrayList<>();
        }

        gamificationDAO.updateUserXP(userId, xpValue);

        String updateHabit = "UPDATE HABIT SET "
                + "TotalCompletions = TotalCompletions + 1, "
                + "CurrentStreak    = CurrentStreak + 1, "
                + "LongestStreak    = GREATEST(LongestStreak, CurrentStreak + 1) "
                + "WHERE HabitID = ?";
        try (PreparedStatement pst = conn.prepareStatement(updateHabit)) {
            pst.setInt(1, habitId);
            pst.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating habit stats: " + e.getMessage());
        }

        gamificationDAO.updateUserLevel(userId);

        return gamificationDAO.awardBadgesIfEligible(userId, habitId);
    }

    public void deleteUserLog(int logId) {
        String sql = "DELETE FROM HABIT_LOG WHERE LogID = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, logId);
            pst.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting log: " + e.getMessage());
        }
    }

    @Override
    public void viewHabitsByUser(int userId) {
        String sql = "SELECT h.HabitID, h.HabitName, c.CategoryName, h.Frequency, "
                + "h.CurrentStreak, h.TotalCompletions, h.XPValue "
                + "FROM HABIT h JOIN CATEGORY c ON h.CategoryID = c.CategoryID "
                + "WHERE h.UserID = ? ORDER BY h.HabitID";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                System.out.printf("%-5d %-22s %-15s %-10s %-8d %-12d %-5d%n",
                        rs.getInt("HabitID"), rs.getString("HabitName"),
                        rs.getString("CategoryName"), rs.getString("Frequency"),
                        rs.getInt("CurrentStreak"), rs.getInt("TotalCompletions"),
                        rs.getInt("XPValue"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching habits: " + e.getMessage());
        }
    }

    @Override
    public void viewLogsByUser(int userId) {
        String sql = "SELECT l.LogID, h.HabitName, l.CompletionDate, "
                + "l.CompletionTime, l.Notes, l.XPAwarded "
                + "FROM HABIT_LOG l JOIN HABIT h ON l.HabitID = h.HabitID "
                + "WHERE l.UserID = ? ORDER BY l.CompletionDate DESC";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String notes = rs.getString("Notes");
                if (notes == null)
                    notes = "-";
                System.out.printf("%-6d %-22s %-13s %-10s %-15s %-5d%n",
                        rs.getInt("LogID"), rs.getString("HabitName"),
                        rs.getString("CompletionDate"), rs.getString("CompletionTime"),
                        notes, rs.getInt("XPAwarded"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching logs: " + e.getMessage());
        }
    }

}