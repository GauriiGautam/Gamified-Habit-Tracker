package com.habit.main;

import com.habit.db.DBConnection;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class SeedData {
    public static void main(String[] args) {
        String[] queries = {
            "INSERT IGNORE INTO USER (UserID, Username, Email, Password, FullName, Bio, CurrentLevel, TotalXP, RegistrationDate, IsActive) VALUES " +
            "(2, 'johndoe', 'john@test.com', 'password123', 'John Doe', 'Fitness enthusiast', 3, 550, CURDATE(), 1)," +
            "(3, 'janedoe', 'jane@test.com', 'password123', 'Jane Doe', 'Bookworm', 2, 320, CURDATE(), 1)," +
            "(4, 'mike_smith', 'mike@test.com', 'password123', 'Mike Smith', 'Coder and gamer', 4, 850, CURDATE(), 1)," +
            "(5, 'sarah_j', 'sarah@test.com', 'password123', 'Sarah Jones', 'Yoga master', 5, 1200, CURDATE(), 1)," +
            "(6, 'alex_w', 'alex@test.com', 'password123', 'Alex Williams', 'Musician', 2, 210, CURDATE(), 1);",

            "INSERT IGNORE INTO HABIT (HabitID, UserID, CategoryID, HabitName, Frequency, DifficultyLevel, XPValue, CreatedDate, CurrentStreak, LongestStreak, TotalCompletions, IsActive) VALUES " +
            "(1, 2, 1, 'Morning Jog', 'Daily', 3, 20, CURDATE(), 5, 12, 45, 'Active')," +
            "(2, 2, 1, 'Pushups', 'Daily', 2, 15, CURDATE(), 2, 5, 15, 'Active')," +
            "(3, 3, 3, 'Read 20 Pages', 'Daily', 2, 15, CURDATE(), 10, 15, 60, 'Active')," +
            "(4, 4, 2, 'Learn Java', 'Daily', 4, 30, CURDATE(), 8, 8, 25, 'Active')," +
            "(5, 5, 1, 'Yoga', 'Daily', 3, 25, CURDATE(), 20, 30, 100, 'Active');",

            "INSERT IGNORE INTO HABIT_LOG (LogID, HabitID, UserID, CompletionDate, CompletionTime, Notes, XPAwarded) VALUES " +
            "(1, 1, 2, CURDATE(), '08:00:00', 'Felt great!', 20)," +
            "(2, 2, 2, CURDATE(), '08:30:00', 'Getting stronger', 15)," +
            "(3, 3, 3, CURDATE(), '20:00:00', 'Awesome book', 15)," +
            "(4, 4, 4, CURDATE(), '22:00:00', 'Fixed a hard bug', 30)," +
            "(5, 5, 5, CURDATE(), '07:00:00', 'So relaxing', 25);",

            "INSERT IGNORE INTO MOOD_ENTRY (MoodID, UserID, MoodDate, MoodScore, MoodType, Notes) VALUES " +
            "(1, 2, CURDATE() - INTERVAL 1 DAY, 4, 'Happy', 'Had a productive day')," +
            "(2, 2, CURDATE(), 5, 'Excited', 'Ready for the weekend!')," +
            "(3, 3, CURDATE(), 3, 'Neutral', 'Just a normal day')," +
            "(4, 4, CURDATE(), 4, 'Calm', 'Coding went smooth')," +
            "(5, 5, CURDATE(), 5, 'Grateful', 'Yoga helped me center myself')," +
            "(6, 2, CURDATE() - INTERVAL 2 DAY, 2, 'Stressed', 'Too much homework')," +
            "(7, 2, CURDATE() - INTERVAL 3 DAY, 3, 'Neutral', 'Averge day')," +
            "(8, 3, CURDATE() - INTERVAL 4 DAY, 5, 'Excited', 'Got a new project!')," +
            "(9, 4, CURDATE() - INTERVAL 5 DAY, 1, 'Sad', 'Bug took 5 hours to fix')," +
            "(10, 5, CURDATE() - INTERVAL 6 DAY, 5, 'Happy', 'Meditation paid off')," +
            "(11, 2, CURDATE() - INTERVAL 7 DAY, 4, 'Calm', 'Morning run complete');",

            "INSERT IGNORE INTO USER_BADGE (UserBadgeID, UserID, BadgeID, EarnedDate, DisplayOrder) VALUES " +
            "(1, 2, 1, CURDATE() - INTERVAL 5 DAY, 1)," +
            "(2, 3, 2, CURDATE() - INTERVAL 10 DAY, 1)," +
            "(3, 4, 3, CURDATE() - INTERVAL 2 DAY, 1)," +
            "(4, 5, 4, CURDATE() - INTERVAL 20 DAY, 1)," +
            "(5, 5, 1, CURDATE() - INTERVAL 1 DAY, 2);"
        };

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            for (String query : queries) {
                stmt.executeUpdate(query);
            }
            System.out.println("Dummy data inserted successfully into the database!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
