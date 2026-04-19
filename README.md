# 🎯 Gamified Habit Tracker

A Java-based desktop application that helps users build and maintain daily habits through gamification, XP rewards, badges, and leaderboards. Built with Java Swing for GUI and MySQL for database management via JDBC.

---

## 🚀 Features

- 🔐 User Authentication (Login & Register)
- ➕ Add, Update, Delete Habits
- 📅 Log Daily Habit Completions
- 📊 View Habit Statistics & Streaks
- 🏆 Badges & Rewards System
- 🥇 Weekly Leaderboard
- 😊 Mood Entry Tracker
- 🎮 XP & Level Progression System
- 🖥️ Beautiful Dark Theme GUI Dashboard

---

## 🛠️ Tech Stack

| Technology | Usage |
|------------|-------|
| Java 21 | Core Programming Language |
| Java Swing | GUI Framework |
| MySQL 8.0 | Database |
| JDBC | Java-Database Connectivity |
| Eclipse IDE | Development Environment |

---

## 📁 Project Structure
src/com/habit/
├── db/
│ └── DBConnection.java
├── model/
│ ├── User.java
│ ├── AdminUser.java
│ ├── Habit.java
│ └── HabitLog.java
├── interfaces/
│ └── HabitOperations.java
├── exceptions/
│ └── HabitException.java
├── dao/
│ └── HabitDAO.java
├── main/
│ └── Main.java
└── gui/
├── LoginFrame.java
├── RegisterFrame.java
├── DashboardFrame.java
├── AddHabitFrame.java
└── LogHabitFrame.java


## 🗄️ Database Schema

### Tables Used:
- `USER` - Stores user information and XP
- `HABIT` - Stores habit details per user
- `HABIT_LOG` - Logs daily habit completions
- `CATEGORY` - Habit categories
- `BADGE` - Achievement badges
- `USER_BADGE` - Badges earned by users
- `LEVEL` - XP level progression
- `LEADERBOARD` - Weekly/Monthly rankings
- `MOOD_ENTRY` - Daily mood tracking
- `REWARD` - Unlockable rewards
- `GOAL` - User habit goals
- `CHALLENGE` - Group challenges
- `FRIEND` - Friend connections
- `XP_TRANSACTION` - XP history
- `SUBSCRIPTION` - User subscription plans

---

## ⚙️ OOP Concepts Demonstrated

| Concept | Implementation |
|---------|---------------|
| Inheritance | AdminUser extends User |
| Polymorphism | displayInfo() overridden in AdminUser |
| Interface | HabitDAO implements HabitOperations |
| Encapsulation | Private fields with getters/setters |
| Packages | 7 organized packages |
| Exception Handling | Custom HabitException + try-catch blocks |

---

## 🗃️ Database Operations

### DML Operations:
- ✅ INSERT - Register user, Add habit, Log completion
- ✅ UPDATE - Update habit name, Update user XP, Update habit status
- ✅ DELETE - Delete habit, Delete habit log

### DRL Operations:
- ✅ SELECT - View habits with JOIN on CATEGORY
- ✅ SELECT - View logs with JOIN on HABIT
- ✅ SELECT - View statistics with GROUP BY
- ✅ SELECT - View badges with JOIN on BADGE
- ✅ SELECT - View leaderboard with JOIN on USER
- ✅ SELECT - View mood entries
- ✅ SELECT - View all users with JOIN on LEVEL

---

## 🔧 Setup Instructions

### Prerequisites:
- Java JDK 21 or higher
- MySQL 8.0 or higher
- Git

### Steps:

#### 1. Clone the Repository
git clone https://github.com/gaurigautam/Gamified-Habit-Tracker.git
cd Gamified-Habit-Tracker

#### 2. Setup Database
```sql
CREATE DATABASE HabitTracker;
USE HabitTracker;
Run the complete SQL script to create all tables and insert sample data provided in repository.

#### 3. Configure Database Connection
Open this file:
src/com/habit/db/DBConnection.java

Find this line and update your MySQL password:
private static final String PASSWORD = "your_password";
Save the file.

4. Run the Application

Option A: VS Code (Recommended)
1. Open folder in VS Code
2. Install Extension Pack for Java by Microsoft
3. Wait for Java project to load
4. Open src/com/habit/main/Main.java
5. Click Run button top right

- 🛠️ **Troubleshooting Database Connectivity:** If you see a "MySQL Driver not found" error in VS Code or any IDE, go to your project explorer, click on **Referenced Libraries**, and manually select the `mysql-connector-j-9.6.0.jar` from your PC to connect it to your database.

Option B: Eclipse IDE
1. Open Eclipse
2. File → Import → General → Existing Projects into Workspace
3. Browse to this project folder
4. Click Finish
5. Right click project → Build Path → Configure Build Path
6. Add External JARs → Select mysql-connector-j-9.6.0.jar
7. Open src/com/habit/main/Main.java
8. Right click → Run As → Java Application

Option C: Terminal
Windows:
run.bat

Mac/Linux:
chmod +x run.sh
./run.sh

Option D: Double Click Windows
Double click run.bat file
Login window will appear!

⚠️ Important Notes:
MySQL must be running before starting the app
Default MySQL username is root
Only change the PASSWORD field in DBConnection.java
Java JDK 21 or higher required

📥 How to Download MySQL Connector/J (JDBC Driver)
Here are the links and steps to get the exact mysql-connector-j-9.6.0.jar file.

🔗 Option 1: Direct Download Link (Fastest)
Click this link to download the single .jar file directly from the Maven repository:
👉 [Download mysql-connector-j-9.6.0.jar](https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/9.6.0/mysql-connector-j-9.6.0.jar)

🔗 Option 2: Official MySQL Website
Go to: https://dev.mysql.com/downloads/connector/j/
Click "Download"
Select "Platform Independent" (OS doesn't matter)
Download the ZIP file
Extract the .jar file from the ZIP


📸 Application Screenshots

🔐 Login Screen
Dark themed login window
Username and password fields
Register button for new users

🏠 Dashboard
Side navigation menu with all features
Habit table showing streak and XP
Add and Delete habit buttons
User level and XP shown on top

➕ Add Habit Form
Category dropdown loaded from database
Frequency and difficulty selection
XP value input

📅 Log Habit Form
Habit dropdown for current user
Date auto-filled with today's date
Notes input field

🔐 Sample Login Credentials

Username		Password		Level
john_doe		pwd1		Achiever (Level 3)
jane_smith		pwd2		Champion (Level 4)
ravi_k			pwd3		Explorer (Level 2)
```
📄 License
This project is developed for academic purposes as part of Java Mini Project evaluation.
