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

    // ─────────────────────────────────────────────────────────────────
    //  HABIT CRUD
    // ─────────────────────────────────────────────────────────────────

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

    // ─────────────────────────────────────────────────────────────────
    //  HABIT LOGGING — awards XP and updates streak on the Habit row
    // ─────────────────────────────────────────────────────────────────

    @Override
    public void logHabit(int habitId, int userId, String completionDate, String notes) {
        // 1. Get the XP value for this habit
        int xpValue = 10;
        String xpQuery = "SELECT XPValue FROM HABIT WHERE HabitID = ?";
        try (PreparedStatement xpPst = conn.prepareStatement(xpQuery)) {
            xpPst.setInt(1, habitId);
            ResultSet xpRs = xpPst.executeQuery();
            if (xpRs.next()) {
                xpValue = xpRs.getInt("XPValue");
            }
        } catch (SQLException e) {
            System.err.println("Warning: could not read habit XP: " + e.getMessage());
        }

        // 2. Insert the log entry
        String sql = "INSERT INTO HABIT_LOG (LogID, HabitID, UserID, CompletionDate, CompletionTime, Notes, XPAwarded) "
                + "VALUES ((SELECT IFNULL(MAX(LogID),0)+1 FROM HABIT_LOG h2), ?, ?, ?, CURTIME(), ?, ?)";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, habitId);
            pst.setInt(2, userId);
            pst.setString(3, completionDate);
            pst.setString(4, notes);
            pst.setInt(5, xpValue);
            int rows = pst.executeUpdate();
            if (rows > 0) {
                System.out.println("Habit logged successfully for date: " + completionDate);
            }
        } catch (SQLException e) {
            System.err.println("Error logging habit: " + e.getMessage());
            return;
        }

        // 3. Add XP to the user
        updateUserXP(userId, xpValue);

        // 4. Increment TotalCompletions and CurrentStreak on the habit
        String updateHabit = "UPDATE HABIT SET TotalCompletions = TotalCompletions + 1, "
                + "CurrentStreak = CurrentStreak + 1, "
                + "LongestStreak = GREATEST(LongestStreak, CurrentStreak + 1) "
                + "WHERE HabitID = ?";
        try (PreparedStatement pst = conn.prepareStatement(updateHabit)) {
            pst.setInt(1, habitId);
            pst.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating habit stats: " + e.getMessage());
        }

        // 5. Update user's level based on new XP
        updateUserLevel(userId);

        // 6. Award badges if eligible
        awardBadgesIfEligible(userId, habitId);
    }

    // ─────────────────────────────────────────────────────────────────
    //  USER MANAGEMENT
    // ─────────────────────────────────────────────────────────────────

    /**
     * Returns the next available UserID (max + 1).
     */
    public int getNextUserId() {
        String sql = "SELECT IFNULL(MAX(UserID), 0) + 1 AS NextID FROM USER";
        try (Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("NextID");
            }
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
            int rows = pst.executeUpdate();
            if (rows > 0) {
                System.out.println("User '" + username + "' registered successfully.");
                // Refresh leaderboard so new user appears immediately
                refreshLeaderboard();
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.err.println("User ID already exists. Try a different ID.");
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

    /**
     * Recalculates and sets CurrentLevel for the user based on their TotalXP
     * vs the LEVEL table thresholds.
     */
    public void updateUserLevel(int userId) {
        String sql = "UPDATE USER SET CurrentLevel = ("
                + "  SELECT LevelID FROM LEVEL "
                + "  WHERE XPRequired <= (SELECT TotalXP FROM USER WHERE UserID = ?) "
                + "  ORDER BY XPRequired DESC LIMIT 1"
                + ") WHERE UserID = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, userId);
            pst.setInt(2, userId);
            pst.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating user level: " + e.getMessage());
        }
    }

    public void deleteUserLog(int logId) {
        String sql = "DELETE FROM HABIT_LOG WHERE LogID = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, logId);
            int rows = pst.executeUpdate();
            if (rows > 0) {
                System.out.println("Log ID " + logId + " deleted successfully.");
            }
        } catch (SQLException e) {
            System.err.println("Error deleting log: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────
    //  LEADERBOARD — live rebuild from USER table
    // ─────────────────────────────────────────────────────────────────

    /**
     * Re-populates the LEADERBOARD table using live USER XP data.
     * Called after every new registration and habit log.
     */
    public void refreshLeaderboard() {
        try {
            // Clear old weekly entries
            String deleteSql = "DELETE FROM LEADERBOARD WHERE PeriodType = 'alltime'";
            try (Statement st = conn.createStatement()) {
                st.executeUpdate(deleteSql);
            }

            // Insert fresh rankings from live USER data
            String insertSql = "INSERT INTO LEADERBOARD (LeaderboardID, UserID, PeriodType, PeriodStart, PeriodEnd, TotalXP, RankPosition, HabitsCompleted) "
                    + "SELECT "
                    + "  (SELECT IFNULL(MAX(LeaderboardID),0) FROM LEADERBOARD) + ROW_NUMBER() OVER (ORDER BY u.TotalXP DESC), "
                    + "  u.UserID, 'alltime', CURDATE(), CURDATE(), u.TotalXP, "
                    + "  ROW_NUMBER() OVER (ORDER BY u.TotalXP DESC), "
                    + "  (SELECT COUNT(*) FROM HABIT_LOG hl WHERE hl.UserID = u.UserID) "
                    + "FROM USER u WHERE u.IsActive = 1";
            try (Statement st = conn.createStatement()) {
                st.executeUpdate(insertSql);
            }
        } catch (SQLException e) {
            System.err.println("Error refreshing leaderboard: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────
    //  MOOD ENTRIES — full CRUD for the current user
    // ─────────────────────────────────────────────────────────────────

    /**
     * Returns the next available MoodID.
     */
    public int getNextMoodId() {
        String sql = "SELECT IFNULL(MAX(MoodID), 0) + 1 AS NextID FROM MOOD_ENTRY";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt("NextID");
        } catch (SQLException e) {
            System.err.println("Error getting next mood ID: " + e.getMessage());
        }
        return 1;
    }

    /**
     * Adds a new mood entry for the logged-in user (today's date).
     */
    public boolean addMoodEntry(int userId, int moodScore, String moodType, String notes) {
        // Prevent duplicate entry for same date
        String checkSql = "SELECT COUNT(*) FROM MOOD_ENTRY WHERE UserID = ? AND MoodDate = CURDATE()";
        try (PreparedStatement chk = conn.prepareStatement(checkSql)) {
            chk.setInt(1, userId);
            ResultSet chkRs = chk.executeQuery();
            if (chkRs.next() && chkRs.getInt(1) > 0) {
                return false; // already logged today
            }
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
            int rows = pst.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Error adding mood entry: " + e.getMessage());
            return false;
        }
    }

    /**
     * Updates an existing mood entry (only if it belongs to this user).
     */
    public boolean updateMoodEntry(int moodId, int userId, int moodScore, String moodType, String notes) {
        String sql = "UPDATE MOOD_ENTRY SET MoodScore = ?, MoodType = ?, Notes = ? "
                + "WHERE MoodID = ? AND UserID = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, moodScore);
            pst.setString(2, moodType);
            pst.setString(3, notes.isEmpty() ? null : notes);
            pst.setInt(4, moodId);
            pst.setInt(5, userId);
            int rows = pst.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating mood entry: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes a mood entry (only if it belongs to this user).
     */
    public boolean deleteMoodEntry(int moodId, int userId) {
        String sql = "DELETE FROM MOOD_ENTRY WHERE MoodID = ? AND UserID = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, moodId);
            pst.setInt(2, userId);
            int rows = pst.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting mood entry: " + e.getMessage());
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────────────
    //  BADGE AWARDING — automatic based on completion milestones
    // ─────────────────────────────────────────────────────────────────

    /**
     * Checks all badge criteria and awards any not yet earned by this user.
     * Called automatically after every habit log.
     */
    public void awardBadgesIfEligible(int userId, int habitId) {
        try {
            // Fetch current streak and total completions for this habit
            int currentStreak = 0;
            int totalCompletions = 0;
            String habitSql = "SELECT CurrentStreak, TotalCompletions FROM HABIT WHERE HabitID = ?";
            try (PreparedStatement pst = conn.prepareStatement(habitSql)) {
                pst.setInt(1, habitId);
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    currentStreak = rs.getInt("CurrentStreak");
                    totalCompletions = rs.getInt("TotalCompletions");
                }
            }

            // Fetch all active badges
            String badgeSql = "SELECT BadgeID, BadgeType, CriteriaValue, XPReward FROM BADGE WHERE IsActive = 1";
            try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(badgeSql)) {
                while (rs.next()) {
                    int badgeId = rs.getInt("BadgeID");
                    String badgeType = rs.getString("BadgeType");
                    int criteriaValue = rs.getInt("CriteriaValue");
                    int xpReward = rs.getInt("XPReward");

                    boolean eligible = false;
                    if ("Streak".equals(badgeType) && currentStreak >= criteriaValue) {
                        eligible = true;
                    } else if ("Completion".equals(badgeType) && totalCompletions >= criteriaValue) {
                        eligible = true;
                    } else if ("Milestone".equals(badgeType) && currentStreak >= criteriaValue) {
                        eligible = true;
                    }

                    if (eligible && !hasEarnedBadge(userId, badgeId)) {
                        grantBadge(userId, badgeId, xpReward);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking badge eligibility: " + e.getMessage());
        }
    }

    private boolean hasEarnedBadge(int userId, int badgeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM USER_BADGE WHERE UserID = ? AND BadgeID = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, userId);
            pst.setInt(2, badgeId);
            ResultSet rs = pst.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    private void grantBadge(int userId, int badgeId, int xpReward) throws SQLException {
        // Get next UserBadgeID
        int nextId = 1;
        String idSql = "SELECT IFNULL(MAX(UserBadgeID), 0) + 1 FROM USER_BADGE";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(idSql)) {
            if (rs.next()) nextId = rs.getInt(1);
        }

        String sql = "INSERT INTO USER_BADGE (UserBadgeID, UserID, BadgeID, EarnedDate, DisplayOrder) "
                + "VALUES (?, ?, ?, CURDATE(), 0)";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, nextId);
            pst.setInt(2, userId);
            pst.setInt(3, badgeId);
            pst.executeUpdate();
        }

        // Also credit the badge XP bonus
        updateUserXP(userId, xpReward);
        updateUserLevel(userId);
        System.out.println("Badge " + badgeId + " awarded to user " + userId + "! +" + xpReward + " XP");
    }

    // ─────────────────────────────────────────────────────────────────
    //  VIEW METHODS (console-style, kept for compatibility)
    // ─────────────────────────────────────────────────────────────────

    @Override
    public void viewHabitsByUser(int userId) {
        String sql = "SELECT h.HabitID, h.HabitName, c.CategoryName, h.Frequency, "
                + "h.CurrentStreak, h.TotalCompletions, h.XPValue, h.IsActive "
                + "FROM HABIT h JOIN CATEGORY c ON h.CategoryID = c.CategoryID "
                + "WHERE h.UserID = ? ORDER BY h.HabitID";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            System.out.println("\n" + "=".repeat(80));
            System.out.println("                        HABITS FOR USER ID: " + userId);
            System.out.println("=".repeat(80));
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("%-5d %-22s %-15s %-10s %-8d %-12d %-5d%n",
                        rs.getInt("HabitID"), rs.getString("HabitName"),
                        rs.getString("CategoryName"), rs.getString("Frequency"),
                        rs.getInt("CurrentStreak"), rs.getInt("TotalCompletions"),
                        rs.getInt("XPValue"));
            }
            if (!found) System.out.println("No habits found for user ID: " + userId);
            System.out.println("=".repeat(80));
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
            System.out.println("\n" + "=".repeat(75));
            System.out.println("                     HABIT LOGS FOR USER ID: " + userId);
            System.out.println("=".repeat(75));
            while (rs.next()) {
                String notes = rs.getString("Notes");
                if (notes == null) notes = "-";
                if (notes.length() > 14) notes = notes.substring(0, 11) + "...";
                System.out.printf("%-6d %-22s %-13s %-10s %-15s %-5d%n",
                        rs.getInt("LogID"), rs.getString("HabitName"),
                        rs.getString("CompletionDate"), rs.getString("CompletionTime"),
                        notes, rs.getInt("XPAwarded"));
            }
            System.out.println("=".repeat(75));
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
            System.out.println("\n" + "=".repeat(80));
            System.out.println("                           ALL USERS");
            System.out.println("=".repeat(80));
            while (rs.next()) {
                System.out.printf("%-6d %-15s %-28s %-15s %-8d %-10s%n",
                        rs.getInt("UserID"), rs.getString("Username"),
                        rs.getString("Email"), rs.getString("LevelName"),
                        rs.getInt("TotalXP"),
                        rs.getInt("IsActive") == 1 ? "Yes" : "No");
            }
            System.out.println("=".repeat(80));
        } catch (SQLException e) {
            System.err.println("Error fetching users: " + e.getMessage());
        }
    }

    public void viewLeaderboard() {
        String sql = "SELECT u.Username, u.TotalXP, l.LevelName, "
                + "COUNT(hl.LogID) AS HabitsCompleted "
                + "FROM USER u "
                + "JOIN LEVEL l ON u.CurrentLevel = l.LevelID "
                + "LEFT JOIN HABIT_LOG hl ON u.UserID = hl.UserID "
                + "WHERE u.IsActive = 1 "
                + "GROUP BY u.UserID, u.Username, u.TotalXP, l.LevelName "
                + "ORDER BY u.TotalXP DESC";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            System.out.println("\n" + "=".repeat(65));
            System.out.println("                  ALL-TIME LEADERBOARD");
            System.out.println("=".repeat(65));
            int rank = 1;
            while (rs.next()) {
                System.out.printf("#%-5d %-18s %-10d %-15d%n", rank++,
                        rs.getString("Username"), rs.getInt("TotalXP"),
                        rs.getInt("HabitsCompleted"));
            }
            System.out.println("=".repeat(65));
        } catch (SQLException e) {
            System.err.println("Error fetching leaderboard: " + e.getMessage());
        }
    }

    public void viewUserBadges(int userId) {
        String sql = "SELECT b.BadgeName, b.BadgeType, b.XPReward, "
                + "b.RarityLevel, ub.EarnedDate "
                + "FROM USER_BADGE ub JOIN BADGE b ON ub.BadgeID = b.BadgeID "
                + "WHERE ub.UserID = ? ORDER BY ub.EarnedDate DESC";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            System.out.println("\n" + "=".repeat(65));
            System.out.println("               BADGES FOR USER ID: " + userId);
            System.out.println("=".repeat(65));
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("%-22s %-12s %-8d %-12s %-12s%n",
                        rs.getString("BadgeName"), rs.getString("BadgeType"),
                        rs.getInt("XPReward"), rs.getString("RarityLevel"),
                        rs.getString("EarnedDate"));
            }
            if (!found) System.out.println("No badges earned yet.");
            System.out.println("=".repeat(65));
        } catch (SQLException e) {
            System.err.println("Error fetching badges: " + e.getMessage());
        }
    }

    public void viewHabitStats(int userId) {
        String sql = "SELECT h.HabitName, h.CurrentStreak, h.LongestStreak, "
                + "h.TotalCompletions, h.XPValue "
                + "FROM HABIT h WHERE h.UserID = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            System.out.println("\n" + "=".repeat(75));
            System.out.println("                  HABIT STATISTICS FOR USER: " + userId);
            System.out.println("=".repeat(75));
            while (rs.next()) {
                System.out.printf("%-22s %-10d %-12d %-12d %-10d%n",
                        rs.getString("HabitName"), rs.getInt("CurrentStreak"),
                        rs.getInt("LongestStreak"), rs.getInt("TotalCompletions"),
                        rs.getInt("XPValue"));
            }
            System.out.println("=".repeat(75));
        } catch (SQLException e) {
            System.err.println("Error fetching habit stats: " + e.getMessage());
        }
    }

    public void viewMoodEntries(int userId) {
        String sql = "SELECT MoodID, MoodDate, MoodScore, MoodType, Notes "
                + "FROM MOOD_ENTRY WHERE UserID = ? ORDER BY MoodDate DESC";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            System.out.println("\n" + "=".repeat(60));
            System.out.println("              MOOD ENTRIES FOR USER: " + userId);
            System.out.println("=".repeat(60));
            while (rs.next()) {
                String notes = rs.getString("Notes");
                if (notes == null) notes = "-";
                System.out.printf("%-5d %-13s %-8d %-12s %-25s%n",
                        rs.getInt("MoodID"), rs.getString("MoodDate"),
                        rs.getInt("MoodScore"), rs.getString("MoodType"), notes);
            }
            System.out.println("=".repeat(60));
        } catch (SQLException e) {
            System.err.println("Error fetching mood entries: " + e.getMessage());
        }
    }

    public void viewCategories() {
        String sql = "SELECT CategoryID, CategoryName, Description FROM CATEGORY";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            System.out.println("\n" + "=".repeat(55));
            System.out.println("               ALL CATEGORIES");
            System.out.println("=".repeat(55));
            while (rs.next()) {
                System.out.printf("%-5d %-18s %-30s%n",
                        rs.getInt("CategoryID"), rs.getString("CategoryName"),
                        rs.getString("Description"));
            }
            System.out.println("=".repeat(55));
        } catch (SQLException e) {
            System.err.println("Error fetching categories: " + e.getMessage());
        }
    }
}