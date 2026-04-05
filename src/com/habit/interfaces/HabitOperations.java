package com.habit.interfaces;

public interface HabitOperations {
    void addHabit(int userId, int categoryId, String habitName, String frequency, int difficultyLevel, int xpValue);

    void updateHabit(int habitId, String newName);

    void deleteHabit(int habitId);

    void viewHabitsByUser(int userId);

    void logHabit(int habitId, int userId, String completionDate, String notes);

    void viewLogsByUser(int userId);
}