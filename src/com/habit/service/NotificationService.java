package com.habit.service;

import com.habit.dao.HabitDAO;
import com.habit.model.Habit;
import com.habit.model.User;

import java.util.List;

/**
 * Notification and Reminder Module
 * Responsible for checking scheduled reminders, streak warnings, and sending alerts.
 * Note: In a real headless desktop app, this might poll via a Thread.
 */
public class NotificationService {

    private HabitDAO habitDAO;

    public NotificationService() {
        this.habitDAO = new HabitDAO();
    }

    /**
     * Checks if any habits for a user are due for a reminder.
     * Could be hooked into a Swing Timer or a background Worker Thread.
     */
    public void checkForReminders(User user) {
        if (user == null || !user.isActive()) return;
        
        String prefs = user.getNotificationPreferences();
        if (prefs != null && prefs.contains("\"enabled\":false")) {
            return; // User muted notifications
        }

        List<Habit> activeHabits = habitDAO.getHabitsByUserId(user.getUserId());
        if (activeHabits == null) return;

        // In a real implementation, we would compare the habit's reminderTime 
        // to the LocalTime.now() and trigger an alert if within a threshold.
        
        for (Habit habit : activeHabits) {
            if (habit.getReminderTime() != null) {
                System.out.println("DEBUG: Pinging reminder for Habit [" + habit.getHabitName() + "] at " + habit.getReminderTime());
            }
        }
    }

    /**
     * Triggers a localized desktop notification.
     * A basic CLI printout for the headless backend logic. 
     * The Swing UI will hijack this and use 'JOptionPane' or native Tray alerts.
     */
    public void sendNotification(String title, String message) {
        System.out.println("========== NOTIFICATION ==========");
        System.out.println(title);
        System.out.println(message);
        System.out.println("==================================");
    }
}
