import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class RunSQL {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/gamified_habits?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        String user = "root";
        String pass = "Gauri123";

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             Statement stmt = conn.createStatement()) {
            
            System.out.println("Creating tables...");
            
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS User (" +
                "UserID BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "Username VARCHAR(50) NOT NULL UNIQUE," +
                "Email VARCHAR(100) NOT NULL UNIQUE," +
                "Password VARCHAR(255) NOT NULL," +
                "FullName VARCHAR(100)," +
                "Bio TEXT," +
                "AvatarURL VARCHAR(255)," +
                "CurrentLevel INT DEFAULT 1," +
                "TotalXP INT DEFAULT 0," +
                "RegistrationDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "LastLoginDate TIMESTAMP NULL," +
                "IsActive BOOLEAN DEFAULT TRUE," +
                "NotificationPreferences JSON NULL," +
                "AIPredictionEnabled BOOLEAN DEFAULT TRUE," +
                "AnomalyDetectionEnabled BOOLEAN DEFAULT TRUE," +
                "AdaptiveDifficultyEnabled BOOLEAN DEFAULT TRUE" +
                ")");
                
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Category (" +
                "CategoryID INT AUTO_INCREMENT PRIMARY KEY," +
                "CategoryName VARCHAR(50) NOT NULL UNIQUE," +
                "Description TEXT" +
                ")");
                
            // Insert default category if not exists
            try { stmt.executeUpdate("INSERT IGNORE INTO Category (CategoryName) VALUES ('General')"); } catch (Exception e) {}

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Habit (" +
                "HabitID BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "UserID BIGINT," +
                "CategoryID INT," +
                "HabitName VARCHAR(100) NOT NULL," +
                "Description TEXT," +
                "Frequency VARCHAR(50)," +
                "TargetCount INT DEFAULT 1," +
                "DifficultyLevel INT DEFAULT 2," +
                "XPValue INT DEFAULT 10," +
                "CurrentStreak INT DEFAULT 0," +
                "LongestStreak INT DEFAULT 0," +
                "TotalCompletions INT DEFAULT 0," +
                "IsActive VARCHAR(20) DEFAULT 'ACTIVE'," +
                "CreatedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "ReminderTime TIME NULL," +
                "IconName VARCHAR(50)," +
                "AdaptiveDifficultyEnabled BOOLEAN DEFAULT TRUE," +
                "CurrentDifficultyMultiplier FLOAT DEFAULT 1.0," +
                "OptimalDifficultyScore FLOAT DEFAULT 0.0," +
                "FOREIGN KEY (UserID) REFERENCES User(UserID)," +
                "FOREIGN KEY (CategoryID) REFERENCES Category(CategoryID)" +
                ")");
                
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Goal (" +
                "GoalID BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "HabitID BIGINT," +
                "GoalType VARCHAR(50)," +
                "TargetCompletions INT," +
                "StartDate DATE," +
                "EndDate DATE," +
                "Status VARCHAR(20) DEFAULT 'IN_PROGRESS'," +
                "CompletedCount INT DEFAULT 0," +
                "CreatedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (HabitID) REFERENCES Habit(HabitID)" +
                ")");
                
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Habit_Log (" +
                "LogID BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "HabitID BIGINT," +
                "UserID BIGINT," +
                "CompletionDate DATE," +
                "CompletionTime TIME," +
                "Notes TEXT," +
                "XPAwarded INT DEFAULT 0," +
                "MoodAtCompletion INT NULL," +
                "DifficultyAtCompletion FLOAT DEFAULT 1.0," +
                "IsEdited BOOLEAN DEFAULT FALSE," +
                "CreatedTimestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (HabitID) REFERENCES Habit(HabitID)," +
                "FOREIGN KEY (UserID) REFERENCES User(UserID)" +
                ")");
                
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Level (" +
                "LevelNumber INT PRIMARY KEY," +
                "XPRequired INT NOT NULL," +
                "PerkDescription TEXT" +
                ")");
                
            // Insert typical levels
            try {
                stmt.executeUpdate("INSERT IGNORE INTO Level (LevelNumber, XPRequired, PerkDescription) VALUES " +
                    "(1, 0, 'Novice'), (2, 100, 'Beginner'), (3, 300, 'Apprentice'), (4, 600, 'Dedicated'), (5, 1000, 'Master')");
            } catch(Exception e) {}
                
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Streak_Insurance (" +
                "InsuranceID BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "UserID BIGINT," +
                "HabitID BIGINT," +
                "TotalTokens INT DEFAULT 0," +
                "UsedTokens INT DEFAULT 0," +
                "LastUsedDate TIMESTAMP NULL," +
                "EarnedSource VARCHAR(100)," +
                "FOREIGN KEY (UserID) REFERENCES User(UserID)," +
                "FOREIGN KEY (HabitID) REFERENCES Habit(HabitID)" +
                ")");

            System.out.println("Tables created successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
