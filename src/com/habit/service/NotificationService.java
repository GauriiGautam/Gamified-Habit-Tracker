package com.habit.service;

import com.habit.dao.HabitDAO;
import com.habit.model.Habit;
import com.habit.model.User;

import java.util.List;


public class NotificationService {

    private HabitDAO habitDAO;

    public NotificationService() {
        this.habitDAO = new HabitDAO();
    }

    
    public void checkForReminders(User user) {
        if (user == null || !user.isActive()) return;
        
        String prefs = user.getNotificationPreferences();
        if (prefs != null && prefs.contains("\"enabled\":false")) {
            return; 
        }

        List<Habit> activeHabits = habitDAO.getHabitsByUserId(user.getUserId());
        if (activeHabits == null) return;

        
        
        for (Habit habit : activeHabits) {
            if (habit.getReminderTime() != null) {
                System.out.println("DEBUG: Pinging reminder for Habit [" + habit.getHabitName() + "] at " + habit.getReminderTime());
            }
        }
    }

    
    public void sendNotification(String title, String message) {
        System.out.println("========== NOTIFICATION ==========");
        System.out.println(title);
        System.out.println(message);
        System.out.println("==================================");
    }
}
