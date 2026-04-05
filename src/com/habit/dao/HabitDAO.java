package com.habit.dao;

import com.habit.db.DBConnection;
import com.habit.interfaces.HabitOperations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class HabitDAO implements HabitOperations {

    private Connection conn;

    public HabitDAO() {
        try {
            this.conn = DBConnection.getConnection();
        } catch (SQLException e) {
            System.err.println("Failed to initialize DAO: " + e.getMessage());
        }
    }

    @Override
    public void addHabit(int userId, int categoryId, String habitName,
            String frequency, int difficultyLevel, int xpValue) {
        String sql = "INSERT INTO HABIT (HabitID, UserID, CategoryID, HabitName, Frequency, DifficultyLevel, XPValue, CreatedDate, IsActive) "
                +
                "VALUES ((SELECT IFNULL(MAX(HabitID),0)+1 FROM HABIT h2), ?, ?, ?, ?, ?, ?, CURDATE(), 'Active')";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, userId);
            pst.setInt(2, categoryId);
            pst.setString(3, habitName);
            pst.setString(4, frequency);
            pst.setInt(5, difficultyLevel);
            pst.setInt(6, xpValue);
            int rows = pst.executeUpdate();
            if (rows > 0) {
                System.out.println("Habit '" + habitName + "' added successfully.");
            }
        } catch (SQLException e) {
            System.err.println("Error adding habit: " + e.getMessage());
        }
    }

    public void addUser(int userId, String username, String email,
            String password, String fullName, String bio, int levelId) {
        String sql = "INSERT INTO USER (UserID, Username, Email, Password, FullName, Bio, CurrentLevel, TotalXP, RegistrationDate, IsActive) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?, 0, CURDATE(), 1)";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, userId);
            pst.setString(2, username);
            pst.setString(3, email);
            pst.setString(4, password);
            pst.setString(5, fullName);
            pst.setString(6, bio);
            pst.setInt(7, levelId);
            int rows = pst.executeUpdate();
            if (rows > 0) {
                System.out.println("User '" + username + "' registered successfully.");
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.err.println("User ID already exists. Try a different ID.");
            } else {
                System.err.println("Error adding user: " + e.getMessage());
            }
        }
    }

    @Override
    public void logHabit(int habitId, int userId, String completionDate, String notes) {
        String sql = "INSERT INTO HABIT_LOG (LogID, HabitID, UserID, CompletionDate, CompletionTime, Notes, XPAwarded) "
                +
                "VALUES ((SELECT IFNULL(MAX(LogID),0)+1 FROM HABIT_LOG h2), ?, ?, ?, CURTIME(), ?, 10)";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, habitId);
            pst.setInt(2, userId);
            pst.setString(3, completionDate);
            pst.setString(4, notes);
            int rows = pst.executeUpdate();
            if (rows > 0) {
                System.out.println("Habit logged successfully for date: " + completionDate);
            }
        } catch (SQLException e) {
            System.err.println("Error logging habit: " + e.getMessage());
        }
    }

    @Override
    public void updateHabit(int habitId, String newName) {
        String sql = "UPDATE HABIT SET HabitName = ? WHERE HabitID = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, newName);
            pst.setInt(2, habitId);
            int rows = pst.executeUpdate();
            if (rows > 0) {
                System.out.println("Habit updated to: " + newName);
            } else {
                System.out.println("No habit found with ID: " + habitId);
            }
        } catch (SQLException e) {
            System.err.println("Error updating habit: " + e.getMessage());
        }
    }

    public void updateUserXP(int userId, int xpToAdd) {
        String sql = "UPDATE USER SET TotalXP = TotalXP + ? WHERE UserID = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, xpToAdd);
            pst.setInt(2, userId);
            int rows = pst.executeUpdate();
            if (rows > 0) {
                System.out.println("XP updated. Added " + xpToAdd + " XP to user " + userId);
            }
        } catch (SQLException e) {
            System.err.println("Error updating XP: " + e.getMessage());
        }
    }

    public void updateHabitStatus(int habitId, String status) {
        String sql = "UPDATE HABIT SET IsActive = ? WHERE HabitID = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, status);
            pst.setInt(2, habitId);
            int rows = pst.executeUpdate();
            if (rows > 0) {
                System.out.println("Habit status updated to: " + status);
            }
        } catch (SQLException e) {
            System.err.println("Error updating habit status: " + e.getMessage());
        }
    }

    @Override
    public void deleteHabit(int habitId) {
        String sql = "DELETE FROM HABIT WHERE HabitID = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, habitId);
            int rows = pst.executeUpdate();
            if (rows > 0) {
                System.out.println("Habit ID " + habitId + " deleted successfully.");
            } else {
                System.out.println("No habit found with ID: " + habitId);
            }
        } catch (SQLException e) {
            System.err.println("Error deleting habit: " + e.getMessage());
        }
    }

    public void deleteUserLog(int logId) {
        String sql = "DELETE FROM HABIT_LOG WHERE LogID = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, logId);
            int rows = pst.executeUpdate();
            if (rows > 0) {
                System.out.println("Log ID " + logId + " deleted successfully.");
            } else {
                System.out.println("No log found with ID: " + logId);
            }
        } catch (SQLException e) {
            System.err.println("Error deleting log: " + e.getMessage());
        }
    }

    @Override
    public void viewHabitsByUser(int userId) {
        String sql = "SELECT h.HabitID, h.HabitName, c.CategoryName, h.Frequency, " +
                "h.CurrentStreak, h.TotalCompletions, h.XPValue, h.IsActive " +
                "FROM HABIT h JOIN CATEGORY c ON h.CategoryID = c.CategoryID " +
                "WHERE h.UserID = ? ORDER BY h.HabitID";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            System.out.println("\n" + "=".repeat(80));
            System.out.println("                        HABITS FOR USER ID: " + userId);
            System.out.println("=".repeat(80));
            System.out.printf("%-5s %-22s %-15s %-10s %-8s %-12s %-5s%n",
                    "ID", "Habit Name", "Category", "Frequency", "Streak", "Completions", "XP");
            System.out.println("-".repeat(80));
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("%-5d %-22s %-15s %-10s %-8d %-12d %-5d%n",
                        rs.getInt("HabitID"),
                        rs.getString("HabitName"),
                        rs.getString("CategoryName"),
                        rs.getString("Frequency"),
                        rs.getInt("CurrentStreak"),
                        rs.getInt("TotalCompletions"),
                        rs.getInt("XPValue"));
            }
            if (!found) {
                System.out.println("No habits found for user ID: " + userId);
            }
            System.out.println("=".repeat(80));
        } catch (SQLException e) {
            System.err.println("Error fetching habits: " + e.getMessage());
        }
    }

    @Override
    public void viewLogsByUser(int userId) {
        String sql = "SELECT l.LogID, h.HabitName, l.CompletionDate, " +
                "l.CompletionTime, l.Notes, l.XPAwarded " +
                "FROM HABIT_LOG l JOIN HABIT h ON l.HabitID = h.HabitID " +
                "WHERE l.UserID = ? ORDER BY l.CompletionDate DESC";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            System.out.println("\n" + "=".repeat(75));
            System.out.println("                     HABIT LOGS FOR USER ID: " + userId);
            System.out.println("=".repeat(75));
            System.out.printf("%-6s %-22s %-13s %-10s %-15s %-5s%n",
                    "LogID", "Habit Name", "Date", "Time", "Notes", "XP");
            System.out.println("-".repeat(75));
            boolean found = false;
            while (rs.next()) {
                found = true;
                String notes = rs.getString("Notes");
                if (notes == null)
                    notes = "-";
                if (notes.length() > 14)
                    notes = notes.substring(0, 11) + "...";
                System.out.printf("%-6d %-22s %-13s %-10s %-15s %-5d%n",
                        rs.getInt("LogID"),
                        rs.getString("HabitName"),
                        rs.getString("CompletionDate"),
                        rs.getString("CompletionTime"),
                        notes,
                        rs.getInt("XPAwarded"));
            }
            if (!found) {
                System.out.println("No logs found for user ID: " + userId);
            }
            System.out.println("=".repeat(75));
        } catch (SQLException e) {
            System.err.println("Error fetching logs: " + e.getMessage());
        }
    }

    public void viewAllUsers() {
        String sql = "SELECT u.UserID, u.Username, u.Email, u.FullName, " +
                "u.TotalXP, l.LevelName, u.IsActive " +
                "FROM USER u JOIN LEVEL l ON u.CurrentLevel = l.LevelID " +
                "ORDER BY u.TotalXP DESC";
        try (Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            System.out.println("\n" + "=".repeat(80));
            System.out.println("                           ALL USERS");
            System.out.println("=".repeat(80));
            System.out.printf("%-6s %-15s %-28s %-15s %-8s %-10s%n",
                    "ID", "Username", "Email", "Level", "XP", "Active");
            System.out.println("-".repeat(80));
            while (rs.next()) {
                System.out.printf("%-6d %-15s %-28s %-15s %-8d %-10s%n",
                        rs.getInt("UserID"),
                        rs.getString("Username"),
                        rs.getString("Email"),
                        rs.getString("LevelName"),
                        rs.getInt("TotalXP"),
                        rs.getInt("IsActive") == 1 ? "Yes" : "No");
            }
            System.out.println("=".repeat(80));
        } catch (SQLException e) {
            System.err.println("Error fetching users: " + e.getMessage());
        }
    }

    public void viewLeaderboard() {
        String sql = "SELECT u.Username, l.RankPosition, l.TotalXP, " +
                "l.HabitsCompleted, l.PeriodType " +
                "FROM LEADERBOARD l JOIN USER u ON l.UserID = u.UserID " +
                "WHERE l.PeriodType = 'weekly' ORDER BY l.RankPosition";
        try (Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("               WEEKLY LEADERBOARD");
            System.out.println("=".repeat(60));
            System.out.printf("%-6s %-18s %-10s %-15s%n",
                    "Rank", "Username", "XP", "Habits Done");
            System.out.println("-".repeat(60));
            while (rs.next()) {
                System.out.printf("%-6d %-18s %-10d %-15d%n",
                        rs.getInt("RankPosition"),
                        rs.getString("Username"),
                        rs.getInt("TotalXP"),
                        rs.getInt("HabitsCompleted"));
            }
            System.out.println("=".repeat(60));
        } catch (SQLException e) {
            System.err.println("Error fetching leaderboard: " + e.getMessage());
        }
    }

    public void viewUserBadges(int userId) {
        String sql = "SELECT b.BadgeName, b.BadgeType, b.XPReward, " +
                "b.RarityLevel, ub.EarnedDate " +
                "FROM USER_BADGE ub JOIN BADGE b ON ub.BadgeID = b.BadgeID " +
                "WHERE ub.UserID = ? ORDER BY ub.EarnedDate DESC";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            System.out.println("\n" + "=".repeat(65));
            System.out.println("               BADGES FOR USER ID: " + userId);
            System.out.println("=".repeat(65));
            System.out.printf("%-22s %-12s %-8s %-12s %-12s%n",
                    "Badge Name", "Type", "XP", "Rarity", "Earned Date");
            System.out.println("-".repeat(65));
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("%-22s %-12s %-8d %-12s %-12s%n",
                        rs.getString("BadgeName"),
                        rs.getString("BadgeType"),
                        rs.getInt("XPReward"),
                        rs.getString("RarityLevel"),
                        rs.getString("EarnedDate"));
            }
            if (!found) {
                System.out.println("No badges earned yet for user ID: " + userId);
            }
            System.out.println("=".repeat(65));
        } catch (SQLException e) {
            System.err.println("Error fetching badges: " + e.getMessage());
        }
    }

    public void viewHabitStats(int userId) {
        String sql = "SELECT h.HabitName, h.CurrentStreak, h.LongestStreak, " +
                "h.TotalCompletions, h.XPValue, " +
                "COUNT(l.LogID) as LoggedCount " +
                "FROM HABIT h LEFT JOIN HABIT_LOG l ON h.HabitID = l.HabitID " +
                "WHERE h.UserID = ? GROUP BY h.HabitID, h.HabitName, " +
                "h.CurrentStreak, h.LongestStreak, h.TotalCompletions, h.XPValue";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            System.out.println("\n" + "=".repeat(75));
            System.out.println("                  HABIT STATISTICS FOR USER: " + userId);
            System.out.println("=".repeat(75));
            System.out.printf("%-22s %-10s %-12s %-12s %-10s%n",
                    "Habit Name", "Cur Streak", "Best Streak", "Completions", "XP/log");
            System.out.println("-".repeat(75));
            while (rs.next()) {
                System.out.printf("%-22s %-10d %-12d %-12d %-10d%n",
                        rs.getString("HabitName"),
                        rs.getInt("CurrentStreak"),
                        rs.getInt("LongestStreak"),
                        rs.getInt("TotalCompletions"),
                        rs.getInt("XPValue"));
            }
            System.out.println("=".repeat(75));
        } catch (SQLException e) {
            System.err.println("Error fetching habit stats: " + e.getMessage());
        }
    }

    public void viewMoodEntries(int userId) {
        String sql = "SELECT MoodDate, MoodScore, MoodType, Notes " +
                "FROM MOOD_ENTRY WHERE UserID = ? ORDER BY MoodDate DESC";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            System.out.println("\n" + "=".repeat(60));
            System.out.println("              MOOD ENTRIES FOR USER: " + userId);
            System.out.println("=".repeat(60));
            System.out.printf("%-13s %-8s %-12s %-25s%n",
                    "Date", "Score", "Mood", "Notes");
            System.out.println("-".repeat(60));
            while (rs.next()) {
                String notes = rs.getString("Notes");
                if (notes == null)
                    notes = "-";
                System.out.printf("%-13s %-8d %-12s %-25s%n",
                        rs.getString("MoodDate"),
                        rs.getInt("MoodScore"),
                        rs.getString("MoodType"),
                        notes);
            }
            System.out.println("=".repeat(60));
        } catch (SQLException e) {
            System.err.println("Error fetching mood entries: " + e.getMessage());
        }
    }

    public void viewCategories() {
        String sql = "SELECT CategoryID, CategoryName, Description FROM CATEGORY";
        try (Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            System.out.println("\n" + "=".repeat(55));
            System.out.println("               ALL CATEGORIES");
            System.out.println("=".repeat(55));
            System.out.printf("%-5s %-18s %-30s%n", "ID", "Name", "Description");
            System.out.println("-".repeat(55));
            while (rs.next()) {
                System.out.printf("%-5d %-18s %-30s%n",
                        rs.getInt("CategoryID"),
                        rs.getString("CategoryName"),
                        rs.getString("Description"));
            }
            System.out.println("=".repeat(55));
        } catch (SQLException e) {
            System.err.println("Error fetching categories: " + e.getMessage());
        }
    }
}