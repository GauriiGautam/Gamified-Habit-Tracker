package com.habit.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionManager {
    

    private static final String URL = "jdbc:mysql://localhost:3306/gamified_habits?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "Gauri123"; 

    
    static {
        try {
           
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("CRITICAL ERROR: MySQL JDBC Driver not found. " +
                               "Make sure mysql-connector-j-8.x.x.jar is in the lib/ folder and added to the classpath.");
            e.printStackTrace();
        }
    }


    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    
    
    public static void main(String[] args) {
        System.out.println("Testing Database Connection...");
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("SUCCESS: Connected to the gamified_habits database!");
            }
        } catch (SQLException e) {
            System.err.println("FAILED: Could not connect to the database.");
            System.err.println("Error Message: " + e.getMessage());
            System.err.println("Please ensure the MySQL server is running and the database exists.");
        }
    }
}
