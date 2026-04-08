package com.habit.dao;

import com.habit.db.DBConnection;

import java.sql.*;

public class MoodDAO {

    private Connection conn;

    public MoodDAO() {
        try {
            this.conn = DBConnection.getConnection();
        } catch (SQLException e) {
            System.err.println("Failed to initialize MoodDAO: " + e.getMessage());
        }
    }

    public int getNextMoodId() {
        String sql = "SELECT IFNULL(MAX(MoodID), 0) + 1 AS NextID FROM MOOD_ENTRY";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next())
                return rs.getInt("NextID");
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
            if (chkRs.next() && chkRs.getInt(1) > 0)
                return false;
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
            int rowsAffected = pst.executeUpdate();
            
            if (rowsAffected > 0) {
                // Feature explicitly requested: re-sequence remaining IDs sequentially
                String reseq = "UPDATE MOOD_ENTRY SET MoodID = MoodID - 1 WHERE MoodID > ?";
                try (PreparedStatement p2 = conn.prepareStatement(reseq)) {
                    p2.setInt(1, moodId);
                    p2.executeUpdate();
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        return false;
    }

    public void viewMoodEntries(int userId) {
        String sql = "SELECT MoodID, MoodDate, MoodScore, MoodType, Notes "
                + "FROM MOOD_ENTRY WHERE UserID = ? ORDER BY MoodDate DESC";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String notes = rs.getString("Notes");
                if (notes == null)
                    notes = "-";
                System.out.printf("%-5d %-13s %-8d %-12s %-25s%n",
                        rs.getInt("MoodID"), rs.getString("MoodDate"),
                        rs.getInt("MoodScore"), rs.getString("MoodType"), notes);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching mood entries: " + e.getMessage());
        }
    }
}
