package com.habit.interfaces;

import java.util.List;

public interface ReminderService {
    void createReminder(int userId, int habitId, String reminderTime);
    void cancelReminder(int reminderId);
    List<String> getDueReminders(int userId);
    void markReminderAsSent(int reminderId);
}
