package com.habit.interfaces;

public interface GamificationSystem {
    void awardXP(int userId, int xpAmount);
    void checkLevelUp(int userId);
    void assignBadge(int userId, int badgeId);
    int calculateNextLevelRequiredXP(int currentLevel);
}
