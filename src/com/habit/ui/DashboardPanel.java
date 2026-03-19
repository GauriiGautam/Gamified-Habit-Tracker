package com.habit.ui;

import com.habit.dao.HabitDAO;
import com.habit.model.Habit;
import com.habit.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class DashboardPanel extends JPanel {

    private MainApplication mainApp;
    private User currentUser;
    private HabitDAO habitDAO;
    private JPanel habitListPanel;

    public DashboardPanel(MainApplication mainApp, User user) {
        this.mainApp = mainApp;
        this.currentUser = user;
        this.habitDAO = new HabitDAO();

        setLayout(new BorderLayout());
        setBackground(new Color(30, 30, 35)); // Modern dark theme

        // Header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Sidebar Configuration (Gamification Stats)
        JPanel sidebarPanel = createSidebarPanel();
        add(sidebarPanel, BorderLayout.WEST);

        // Main Content Area
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel myHabitsLabel = new JLabel("My Active Habits", SwingConstants.LEFT);
        myHabitsLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        myHabitsLabel.setForeground(Color.WHITE);
        contentPanel.add(myHabitsLabel, BorderLayout.NORTH);

        habitListPanel = new JPanel();
        habitListPanel.setLayout(new BoxLayout(habitListPanel, BoxLayout.Y_AXIS));
        habitListPanel.setOpaque(false);
        JScrollPane scrollPane = new JScrollPane(habitListPanel);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
        
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Add Habit Button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        JButton addHabitBtn = new JButton("+ Add New Habit");
        addHabitBtn.setBackground(new Color(46, 204, 113));
        addHabitBtn.setForeground(Color.WHITE);
        addHabitBtn.addActionListener(e -> openHabitManagement());
        bottomPanel.add(addHabitBtn);
        
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(contentPanel, BorderLayout.CENTER);

        loadHabits();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(40, 44, 52));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> mainApp.logout());
        headerPanel.add(logoutBtn, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createSidebarPanel() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(40, 44, 52));
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel userLabel = new JLabel(currentUser.getUsername());
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        userLabel.setForeground(Color.WHITE);
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel levelLabel = new JLabel("Level: " + currentUser.getCurrentLevel());
        levelLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        levelLabel.setForeground(new Color(65, 131, 215));
        levelLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        levelLabel.setBorder(new EmptyBorder(10, 0, 5, 0));

        JLabel xpLabel = new JLabel("XP: " + currentUser.getTotalXp());
        xpLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        xpLabel.setForeground(Color.LIGHT_GRAY);
        xpLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // AI Settings Summary
        JLabel aiLabel = new JLabel("<html><b>AI Services</b><br>LSTM Predictions: " 
            + (currentUser.isAiPredictionEnabled() ? "ON" : "OFF") 
            + "<br>Anomaly Detection: " + (currentUser.isAnomalyDetectionEnabled() ? "ON" : "OFF")
            + "</html>");
        aiLabel.setForeground(Color.GRAY);
        aiLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        aiLabel.setBorder(new EmptyBorder(30, 0, 0, 0));

        sidebar.add(userLabel);
        sidebar.add(levelLabel);
        sidebar.add(xpLabel);
        sidebar.add(aiLabel);

        return sidebar;
    }

    private void loadHabits() {
        habitListPanel.removeAll();
        List<Habit> habits = habitDAO.getHabitsByUserId(currentUser.getUserId());

        if (habits.isEmpty()) {
            JLabel emptyLabel = new JLabel("No active habits yet. Create one!");
            emptyLabel.setForeground(Color.GRAY);
            habitListPanel.add(emptyLabel);
        } else {
            for (Habit habit : habits) {
                JPanel habitCard = new JPanel(new BorderLayout());
                habitCard.setBackground(new Color(50, 54, 62));
                habitCard.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(70, 74, 82), 1),
                    new EmptyBorder(15, 15, 15, 15)
                ));
                habitCard.setMaximumSize(new Dimension(800, 80));

                JLabel nameLabel = new JLabel(habit.getHabitName());
                nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
                nameLabel.setForeground(Color.WHITE);

                JLabel streakLabel = new JLabel("\uD83D\uDD25 Streak: " + habit.getCurrentStreak() + " | XP: +" + habit.getXpValue());
                streakLabel.setForeground(new Color(241, 196, 15)); // Gold

                JButton completeBtn = new JButton("Complete");
                completeBtn.setBackground(new Color(65, 131, 215));
                completeBtn.setForeground(Color.WHITE);
                completeBtn.addActionListener(e -> completeHabit(habit));

                JButton analyticsBtn = new JButton("View AI Insights");
                analyticsBtn.setBackground(new Color(142, 68, 173)); // Purple
                analyticsBtn.setForeground(Color.WHITE);
                analyticsBtn.addActionListener(e -> showAnalytics(habit));
                
                JPanel actionPanel = new JPanel(new FlowLayout());
                actionPanel.setOpaque(false);
                actionPanel.add(analyticsBtn);
                actionPanel.add(completeBtn);

                habitCard.add(nameLabel, BorderLayout.NORTH);
                habitCard.add(streakLabel, BorderLayout.CENTER);
                habitCard.add(actionPanel, BorderLayout.EAST);

                habitListPanel.add(habitCard);
                habitListPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }
        habitListPanel.revalidate();
        habitListPanel.repaint();
    }

    private void openHabitManagement() {
        // Simple direct prompt for basic addition for the monolith
        String name = JOptionPane.showInputDialog(this, "Enter Habit Name:");
        if (name != null && !name.trim().isEmpty()) {
            Habit newHabit = new Habit();
            newHabit.setUserId(currentUser.getUserId());
            newHabit.setHabitName(name.trim());
            newHabit.setCategoryId(1); // Default
            newHabit.setFrequency("Daily");
            newHabit.setDifficultyLevel(2); // Medium
            newHabit.setXpValue(10);
            
            habitDAO.createHabit(newHabit);
            loadHabits();
        }
    }

    private void completeHabit(Habit habit) {
        // Here we would integrate GamificationService and HabitLogDAO
        // Simulating the backend hook for UI purposes:
        JOptionPane.showMessageDialog(this, "Logged completion for: " + habit.getHabitName() + "!\n+" + habit.getXpValue() + " XP Awarded.");
        habit.setCurrentStreak(habit.getCurrentStreak() + 1);
        habitDAO.updateHabitProgress(habit.getHabitId(), habit.getCurrentStreak(), Math.max(habit.getCurrentStreak(), habit.getLongestStreak()), habit.getTotalCompletions() + 1);
        
        // Refresh UI
        loadHabits();
    }

    private void showAnalytics(Habit habit) {
        JDialog dialog = new JDialog(mainApp, "Habit AI Analytics", true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        dialog.add(new AnalyticsPanel(currentUser, habit));
        dialog.setVisible(true);
    }
}
