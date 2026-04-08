package com.habit.dao;

import com.habit.db.DBConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CategoryDAO {

    private Connection conn;

    public CategoryDAO() {
        try {
            this.conn = DBConnection.getConnection();
        } catch (SQLException e) {
            System.err.println("Failed to initialize CategoryDAO: " + e.getMessage());
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
