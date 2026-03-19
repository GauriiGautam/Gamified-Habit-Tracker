package com.habit.ui;

import com.habit.model.User;

import javax.swing.*;
import java.awt.*;


public class MainApplication extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private User loggedInUser;

    public MainApplication() {
        setTitle("AI-Powered Gamified Habit Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768);
        setLocationRelativeTo(null); 
        
     
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        
        mainPanel.add(new LoginPanel(this), "LOGIN");
        
        add(mainPanel);
    }

    
    public void onLoginSuccess(User user) {
        this.loggedInUser = user;
        
      
        DashboardPanel dashboard = new DashboardPanel(this, user);
        mainPanel.add(dashboard, "DASHBOARD");
        

        showView("DASHBOARD");
    }

    
    public void logout() {
        this.loggedInUser = null;
        showView("LOGIN");
    }

    public void showView(String viewName) {
        cardLayout.show(mainPanel, viewName);
    }

    public static void main(String[] args) {
        
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
           
        }

       
        SwingUtilities.invokeLater(() -> {
            MainApplication app = new MainApplication();
            app.setVisible(true);
        });
    }
}
