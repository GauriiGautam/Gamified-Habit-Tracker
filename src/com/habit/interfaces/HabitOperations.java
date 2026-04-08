package com.habit.interfaces;

import java.util.List;

public interface HabitOperations {
    void addHabit(int userId, int categoryId, String habitName, String frequency, int difficultyLevel, int xpValue);

    void updateHabit(int habitId, String newName);

    void updateFullHabit(int habitId, String name, String category, String frequency, int target, int difficulty);

    void deleteHabit(int habitId);

    void viewHabitsByUser(int userId);

    List<String> logHabit(int habitId, int userId, String completionDate, String notes);

    void viewLogsByUser(int userId);
}