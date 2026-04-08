package com.habit.interfaces;

import java.util.List;

public interface ReportingService {
    int getCurrentStreak(int userId, int habitId);
    int getLongestStreak(int userId, int habitId);
    List<String> generateWeeklyReport(int userId);
    double calculateSuccessRate(int userId, int habitId);
}
