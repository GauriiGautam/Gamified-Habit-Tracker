package com.habit.dao;

import com.habit.db.DBConnection;
import com.habit.interfaces.GamificationSystem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GamificationDAO implements GamificationSystem {

    private Connection conn;

    public GamificationDAO() {
        try {
            this.conn = DBConnection.getConnection();
        } catch (SQLException e) {
            System.err.println("Failed to initialize GamificationDAO: " + e.getMessage());
        }
    }

    @Override
    public void awardXP(int userId, int xpAmount) {
        updateUserXP(userId, xpAmount);
    }

    @Override
    public void checkLevelUp(int userId) {
        updateUserLevel(userId);
    }

    @Override
    public void assignBadge(int userId, int badgeId) {
        try {
            grantBadge(userId, badgeId, 0); // basic assign without extra XP, or fetch XP first
        } catch (SQLException e) {
            System.err.println("Error assigning badge: " + e.getMessage());
        }
    }

    @Override
    public int calculateNextLevelRequiredXP(int currentLevel) {
        String sql = "SELECT XPRequired FROM LEVEL WHERE LevelID = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, currentLevel + 1);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) return rs.getInt("XPRequired");
        } catch (SQLException e) {
            System.err.println("Error calculating next level XP: " + e.getMessage());
        }
        return -1;
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
                if (rs.next())
                    totalXP = rs.getInt("TotalXP");
            }

            int levelId = 1;
            try (PreparedStatement pst = conn.prepareStatement(
                    "SELECT LevelID FROM LEVEL WHERE XPRequired <= ? ORDER BY XPRequired DESC LIMIT 1")) {
                pst.setInt(1, totalXP);
                ResultSet rs = pst.executeQuery();
                if (rs.next())
                    levelId = rs.getInt("LevelID");
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

    public List<String> awardBadgesIfEligible(int userId, int habitId) {
        List<String> newlyEarned = new ArrayList<>();

        int currentStreak = 0, totalCompletions = 0;
        try (PreparedStatement pst = conn.prepareStatement(
                "SELECT CurrentStreak, TotalCompletions FROM HABIT WHERE HabitID = ?")) {
            pst.setInt(1, habitId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                currentStreak = rs.getInt("CurrentStreak");
                totalCompletions = rs.getInt("TotalCompletions");
            }
        } catch (SQLException e) {
            System.err.println("Error reading habit stats for badge check: " + e.getMessage());
            return newlyEarned;
        }

        List<int[]> badgeData = new ArrayList<>();
        List<String> badgeTypes = new ArrayList<>();

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT BadgeID, BadgeType, CriteriaValue, XPReward FROM BADGE WHERE IsActive = 1")) {
            while (rs.next()) {
                badgeData.add(new int[] {
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
            int[] data = badgeData.get(i);
            int badgeId = data[0];
            int criteria = data[1];
            int xpReward = data[2];
            String type = badgeTypes.get(i);

            boolean eligible = false;
            if ("Streak".equals(type) && currentStreak >= criteria)
                eligible = true;
            if ("Completion".equals(type) && totalCompletions >= criteria)
                eligible = true;
            if ("Milestone".equals(type) && currentStreak >= criteria)
                eligible = true;

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
            if (rs.next())
                nextId = rs.getInt(1);
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
}
