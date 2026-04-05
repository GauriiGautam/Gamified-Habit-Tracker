# 🎯 Gamified Habit Tracker

A Java-based desktop application that helps users build and maintain daily habits through gamification, XP rewards, badges, and leaderboards. Built with Java Swing for GUI and MySQL for database management via JDBC.

---

## 👥 Team Members

| Name | Role |
|------|------|
| Rachit Doshi | Java GUI Development + JDBC Integration |
| Gauri Gautam | Database Design + SQL Queries |
| Ishan Malviya | DAO Layer + OOP Design |
| Dilip Maurya | Normalization + DBMS Reports |

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
- Eclipse IDE
- MySQL Connector/J 9.6.0

### Steps:

#### 1. Clone the Repository
git clone https://github.com/gaurigautam/Gamified-Habit-Tracker.git

#### 2. Setup Database
```sql
CREATE DATABASE HabitTracker;
USE HabitTracker;
Run the complete SQL script to create all tables and insert sample data.

#### 3. Configure Database Connection
Update `src/com/habit/db/DBConnection.java` with your MySQL credentials:
```java
private static final String URL = "jdbc:mysql://localhost:3306/HabitTracker";
private static final String USER = "root";
private static final String PASSWORD = "your_password";

4. Add MySQL Connector JAR
text

In Eclipse:
Right click project
→ Build Path
→ Configure Build Path
→ Libraries
→ Add External JARs
→ Select mysql-connector-j-9.6.0.jar

5. Run the Application
text

Open src/com/habit/main/Main.java
Press Ctrl + F11 to run
Login window will appear

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
