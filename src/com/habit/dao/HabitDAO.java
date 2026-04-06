package com.habit.dao;

import com.habit.db.DBConnection;
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
            pst.executeUpdate();
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
                pst.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error deleting habit: " + e.getMessage());
        }
    }

    public void updateHabitStatus(int habitId, String status) {
        String sql = "UPDATE HABIT SET IsActive = ? WHERE HabitID = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, status);
            pst.setInt(2, habitId);
            pst.executeUpdate();
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
            if (rs.next()) xpValue = rs.getInt("XPValue");
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
            if (rows == 0) return new ArrayList<>();
        } catch (SQLException e) {
            System.err.println("Error logging habit: " + e.getMessage());
            return new ArrayList<>();
        }


        updateUserXP(userId, xpValue);


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


        updateUserLevel(userId);


        return awardBadgesIfEligible(userId, habitId);
    }






    public int getNextUserId() {
        String sql = "SELECT IFNULL(MAX(UserID), 0) + 1 AS NextID FROM USER";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt("NextID");
        } catch (SQLException e) {
            System.err.println("Error getting next user ID: " + e.getMessage());
        }
        return 1;
    }

    public void addUser(int userId, String username, String email,
            String password, String fullName, String bio, int levelId) {
        String sql = "INSERT INTO USER (UserID, Username, Email, Password, FullName, Bio, CurrentLevel, TotalXP, RegistrationDate, IsActive) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, 0, CURDATE(), 1)";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, userId);
            pst.setString(2, username);
            pst.setString(3, email);
            pst.setString(4, password);
            pst.setString(5, fullName);
            pst.setString(6, bio);
            pst.setInt(7, levelId);
            pst.executeUpdate();
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.err.println("Duplicate user entry: " + e.getMessage());
            } else {
                System.err.println("Error adding user: " + e.getMessage());
            }
        }
    }

    public void updateUserXP(int userId, int xpToAdd) {
        String sql = "UPDATE USER SET TotalXP = TotalXP + ? WHERE UserID = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, xpToAdd);
            pst.setInt(2, userId);
            pst.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating XP: " + e.getMessage());
        }
    }





    public void updateUserLevel(int userId) {
        try {

            int totalXP = 0;
            try (PreparedStatement pst = conn.prepareStatement(
                    "SELECT TotalXP FROM USER WHERE UserID = ?")) {
                pst.setInt(1, userId);
                ResultSet rs = pst.executeQuery();
                if (rs.next()) totalXP = rs.getInt("TotalXP");
            }


            int levelId = 1;
            try (PreparedStatement pst = conn.prepareStatement(
                    "SELECT LevelID FROM LEVEL WHERE XPRequired <= ? ORDER BY XPRequired DESC LIMIT 1")) {
                pst.setInt(1, totalXP);
                ResultSet rs = pst.executeQuery();
                if (rs.next()) levelId = rs.getInt("LevelID");
            }


            try (PreparedStatement pst = conn.prepareStatement(
                    "UPDATE USER SET CurrentLevel = ? WHERE UserID = ?")) {
                pst.setInt(1, levelId);
                pst.setInt(2, userId);
                pst.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error updating user level: " + e.getMessage());
        }
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





    public void refreshLeaderboard() {


    }





    public int getNextMoodId() {
        String sql = "SELECT IFNULL(MAX(MoodID), 0) + 1 AS NextID FROM MOOD_ENTRY";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt("NextID");
        } catch (SQLException e) {
            System.err.println("Error getting next mood ID: " + e.getMessage());
        }
        return 1;
    }





    public boolean addMoodEntry(int userId, int moodScore, String moodType, String notes) {

        try (PreparedStatement chk = conn.prepareStatement(
                "SELECT COUNT(*) FROM MOOD_ENTRY WHERE UserID = ? AND MoodDate = CURDATE()")) {
            chk.setInt(1, userId);
            ResultSet chkRs = chk.executeQuery();
            if (chkRs.next() && chkRs.getInt(1) > 0) return false;
        } catch (SQLException e) {
            System.err.println("Error checking today's mood: " + e.getMessage());
        }

        int nextId = getNextMoodId();
        String sql = "INSERT INTO MOOD_ENTRY (MoodID, UserID, MoodDate, MoodScore, MoodType, Notes) "
                + "VALUES (?, ?, CURDATE(), ?, ?, ?)";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, nextId);
            pst.setInt(2, userId);
            pst.setInt(3, moodScore);
            pst.setString(4, moodType);
            pst.setString(5, notes.isEmpty() ? null : notes);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding mood entry: " + e.getMessage());
            return false;
        }
    }


    public boolean updateMoodEntry(int moodId, int userId, int moodScore, String moodType, String notes) {
        String sql = "UPDATE MOOD_ENTRY SET MoodScore = ?, MoodType = ?, Notes = ? "
                + "WHERE MoodID = ? AND UserID = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, moodScore);
            pst.setString(2, moodType);
            pst.setString(3, notes.isEmpty() ? null : notes);
            pst.setInt(4, moodId);
            pst.setInt(5, userId);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating mood entry: " + e.getMessage());
            return false;
        }
    }


    public boolean deleteMoodEntry(int moodId, int userId) {
        String sql = "DELETE FROM MOOD_ENTRY WHERE MoodID = ? AND UserID = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, moodId);
            pst.setInt(2, userId);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting mood entry: " + e.getMessage());
            return false;
        }
    }










    public List<String> awardBadgesIfEligible(int userId, int habitId) {
        List<String> newlyEarned = new ArrayList<>();

        int currentStreak = 0, totalCompletions = 0;
        try (PreparedStatement pst = conn.prepareStatement(
                "SELECT CurrentStreak, TotalCompletions FROM HABIT WHERE HabitID = ?")) {
            pst.setInt(1, habitId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                currentStreak    = rs.getInt("CurrentStreak");
                totalCompletions = rs.getInt("TotalCompletions");
            }
        } catch (SQLException e) {
            System.err.println("Error reading habit stats for badge check: " + e.getMessage());
            return newlyEarned;
        }


        List<int[]>  badgeData  = new ArrayList<>();
        List<String> badgeTypes = new ArrayList<>();

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                "SELECT BadgeID, BadgeType, CriteriaValue, XPReward FROM BADGE WHERE IsActive = 1")) {
            while (rs.next()) {
                badgeData.add(new int[]{
                    rs.getInt("BadgeID"),
                    rs.getInt("CriteriaValue"),
                    rs.getInt("XPReward")
                });
                badgeTypes.add(rs.getString("BadgeType"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching badges: " + e.getMessage());
            return newlyEarned;
        }


        for (int i = 0; i < badgeData.size(); i++) {
            int[]  data      = badgeData.get(i);
            int    badgeId   = data[0];
            int    criteria  = data[1];
            int    xpReward  = data[2];
            String type      = badgeTypes.get(i);

            boolean eligible = false;
            if ("Streak".equals(type)     && currentStreak    >= criteria) eligible = true;
            if ("Completion".equals(type) && totalCompletions >= criteria) eligible = true;
            if ("Milestone".equals(type)  && currentStreak    >= criteria) eligible = true;

            if (eligible) {
                try {
                    if (!hasEarnedBadge(userId, badgeId)) {
                        grantBadge(userId, badgeId, xpReward);
                        
                        try (PreparedStatement pst = conn.prepareStatement("SELECT BadgeName FROM BADGE WHERE BadgeID=?")) {
                            pst.setInt(1, badgeId);
                            ResultSet bRs = pst.executeQuery();
                            if (bRs.next()) {
                                newlyEarned.add(bRs.getString("BadgeName"));
                            }
                        }
                    }
                } catch (SQLException e) {
                    System.err.println("Error awarding badge: " + e.getMessage());
                }
            }
        }
        return newlyEarned;
    }

    private boolean hasEarnedBadge(int userId, int badgeId) throws SQLException {
        try (PreparedStatement pst = conn.prepareStatement(
                "SELECT COUNT(*) FROM USER_BADGE WHERE UserID = ? AND BadgeID = ?")) {
            pst.setInt(1, userId);
            pst.setInt(2, badgeId);
            ResultSet rs = pst.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    private void grantBadge(int userId, int badgeId, int xpReward) throws SQLException {
        int nextId = 1;
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT IFNULL(MAX(UserBadgeID), 0) + 1 FROM USER_BADGE")) {
            if (rs.next()) nextId = rs.getInt(1);
        }
        try (PreparedStatement pst = conn.prepareStatement(
                "INSERT INTO USER_BADGE (UserBadgeID, UserID, BadgeID, EarnedDate, DisplayOrder) "
                + "VALUES (?, ?, ?, CURDATE(), 0)")) {
            pst.setInt(1, nextId);
            pst.setInt(2, userId);
            pst.setInt(3, badgeId);
            pst.executeUpdate();
        }
        updateUserXP(userId, xpReward);
        updateUserLevel(userId);
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
                if (notes == null) notes = "-";
                System.out.printf("%-6d %-22s %-13s %-10s %-15s %-5d%n",
                        rs.getInt("LogID"), rs.getString("HabitName"),
                        rs.getString("CompletionDate"), rs.getString("CompletionTime"),
                        notes, rs.getInt("XPAwarded"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching logs: " + e.getMessage());
        }
    }

    public void viewAllUsers() {
        String sql = "SELECT u.UserID, u.Username, u.Email, u.FullName, "
                + "u.TotalXP, l.LevelName, u.IsActive "
                + "FROM USER u JOIN LEVEL l ON u.CurrentLevel = l.LevelID "
                + "ORDER BY u.TotalXP DESC";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                System.out.printf("%-6d %-15s %-28s %-15s %-8d %-10s%n",
                        rs.getInt("UserID"), rs.getString("Username"),
                        rs.getString("Email"), rs.getString("LevelName"),
                        rs.getInt("TotalXP"),
                        rs.getInt("IsActive") == 1 ? "Yes" : "No");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching users: " + e.getMessage());
        }
    }

    public void viewLeaderboard() {
        String sql = "SELECT u.Username, u.TotalXP, l.LevelName, COUNT(hl.LogID) AS HabitsCompleted "
                + "FROM USER u JOIN LEVEL l ON u.CurrentLevel = l.LevelID "
                + "LEFT JOIN HABIT_LOG hl ON u.UserID = hl.UserID "
                + "WHERE u.IsActive = 1 GROUP BY u.UserID, u.Username, u.TotalXP, l.LevelName "
                + "ORDER BY u.TotalXP DESC";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            int rank = 1;
            while (rs.next()) {
                System.out.printf("#%-5d %-18s %-10d %-15d%n", rank++,
                        rs.getString("Username"), rs.getInt("TotalXP"),
                        rs.getInt("HabitsCompleted"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching leaderboard: " + e.getMessage());
        }
    }

    public void viewUserBadges(int userId) {
        String sql = "SELECT b.BadgeName, b.BadgeType, b.XPReward, b.RarityLevel, ub.EarnedDate "
                + "FROM USER_BADGE ub JOIN BADGE b ON ub.BadgeID = b.BadgeID "
                + "WHERE ub.UserID = ? ORDER BY ub.EarnedDate DESC";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                System.out.printf("%-22s %-12s %-8d %-12s %-12s%n",
                        rs.getString("BadgeName"), rs.getString("BadgeType"),
                        rs.getInt("XPReward"), rs.getString("RarityLevel"),
                        rs.getString("EarnedDate"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching badges: " + e.getMessage());
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

    public void viewMoodEntries(int userId) {
        String sql = "SELECT MoodID, MoodDate, MoodScore, MoodType, Notes "
                + "FROM MOOD_ENTRY WHERE UserID = ? ORDER BY MoodDate DESC";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String notes = rs.getString("Notes");
                if (notes == null) notes = "-";
                System.out.printf("%-5d %-13s %-8d %-12s %-25s%n",
                        rs.getInt("MoodID"), rs.getString("MoodDate"),
                        rs.getInt("MoodScore"), rs.getString("MoodType"), notes);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching mood entries: " + e.getMessage());
        }
    }

    public void viewCategories() {
        String sql = "SELECT CategoryID, CategoryName, Description FROM CATEGORY";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                System.out.printf("%-5d %-18s %-30s%n",
                        rs.getInt("CategoryID"), rs.getString("CategoryName"),
                        rs.getString("Description"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching categories: " + e.getMessage());
        }
    }
}