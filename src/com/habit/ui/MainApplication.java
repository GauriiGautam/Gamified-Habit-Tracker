package com.habit.ui;

import com.habit.model.User;

import javax.swing.*;
import java.awt.*;

/**
 * MainApplication
 * The core JFrame managing the view transitions.
 */
public class MainApplication extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private User loggedInUser;

    public MainApplication() {
        setTitle("AI-Powered Gamified Habit Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768);
        setLocationRelativeTo(null); // Center on screen
        
        // Use CardLayout to switch between Login, Dashboard, etc.
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Add Views
        mainPanel.add(new LoginPanel(this), "LOGIN");
        
        add(mainPanel);
    }

    /**
     * Called by LoginPanel upon successful authentication.
     */
    public void onLoginSuccess(User user) {
        this.loggedInUser = user;
        
        // Initialize user-specific views
        DashboardPanel dashboard = new DashboardPanel(this, user);
        mainPanel.add(dashboard, "DASHBOARD");
        
        // Switch view
        showView("DASHBOARD");
    }

    /**
     * Logout and return to Login screen.
     */
    public void logout() {
        this.loggedInUser = null;
        showView("LOGIN");
    }

    public void showView(String viewName) {
        cardLayout.show(mainPanel, viewName);
    }

    public static void main(String[] args) {
        // Set modern Look and Feel if available (Nimbus or System)
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, fall back to default
        }

        // Start GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MainApplication app = new MainApplication();
            app.setVisible(true);
        });
    }
}
