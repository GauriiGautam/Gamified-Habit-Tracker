package com.habit.dao;

import com.habit.db.DBConnection;
import com.habit.exceptions.InvalidAuthenticationException;
import com.habit.exceptions.UserNotFoundException;
import com.habit.interfaces.UserOperations;
import com.habit.model.User;

import java.sql.*;

public class UserDAO implements UserOperations {

    private Connection conn;

    public UserDAO() {
        try {
            this.conn = DBConnection.getConnection();
        } catch (SQLException e) {
            System.err.println("Failed to initialize UserDAO: " + e.getMessage());
        }
    }

    public int getNextUserId() {
        String sql = "SELECT IFNULL(MAX(UserID), 0) + 1 AS NextID FROM USER";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next())
                return rs.getInt("NextID");
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
            System.err.println("Error adding user: " + e.getMessage());
        }
    }

    @Override
    public User registerUser(String username, String password, String email, String fullName, String bio) {
        int id = getNextUserId();
        addUser(id, username, email, password, fullName, bio, 1);
        return new User(id, username, email, password, fullName, bio, 1, 0, "", 1);
    }

    @Override
    public User loginUser(String username, String password) throws InvalidAuthenticationException {
        String sql = "SELECT UserID, FullName, Email, Bio, CurrentLevel, TotalXP, RegistrationDate, IsActive FROM USER WHERE Username = ? AND Password = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, username);
            pst.setString(2, password);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("UserID"), username, rs.getString("Email"), password,
                        rs.getString("FullName"), rs.getString("Bio"), rs.getInt("CurrentLevel"),
                        rs.getInt("TotalXP"), rs.getString("RegistrationDate"), rs.getInt("IsActive")
                    );
                } else {
                    throw new InvalidAuthenticationException("Invalid username or password!");
                }
            }
        } catch (SQLException e) {
            throw new InvalidAuthenticationException("Database error during login: " + e.getMessage());
        }
    }

    @Override
    public void updateProfile(int userId, String newEmail, String newPassword) throws UserNotFoundException {
        String sql = "UPDATE USER SET Email = ?, Password = ? WHERE UserID = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, newEmail);
            pst.setString(2, newPassword);
            pst.setInt(3, userId);
            int rows = pst.executeUpdate();
            if (rows == 0) {
                throw new UserNotFoundException("User not found to update.");
            }
        } catch (SQLException e) {
            System.err.println("Error updating profile: " + e.getMessage());
        }
    }

    @Override
    public void deleteUser(int userId) throws UserNotFoundException {
        String sql = "DELETE FROM USER WHERE UserID = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, userId);
            int rows = pst.executeUpdate();
            if (rows == 0) {
                throw new UserNotFoundException("User not found to delete.");
            }
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
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
}
