package com.habit.ui;

import com.habit.dao.UserDAO;
import com.habit.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginPanel extends JPanel {

    private MainApplication mainApp;
    private UserDAO userDAO;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginBtn;
    private JButton registerBtn;
    private JLabel errorLabel;

    public LoginPanel(MainApplication mainApp) {
        this.mainApp = mainApp;
        this.userDAO = new UserDAO();

        setLayout(new GridBagLayout());
        setBackground(new Color(40, 44, 52)); // Modern dark theme

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Habit Tracker Login");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(titleLabel, gbc);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Enterprise AI & Gamification Edition");
        subtitleLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        subtitleLabel.setForeground(new Color(150, 150, 150));
        gbc.gridy = 1;
        add(subtitleLabel, gbc);

        // Username
        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(Color.LIGHT_GRAY);
        gbc.gridy = 2; gbc.gridwidth = 1;
        add(userLabel, gbc);

        usernameField = new JTextField(15);
        gbc.gridx = 1;
        add(usernameField, gbc);

        // Password
        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Color.LIGHT_GRAY);
        gbc.gridx = 0; gbc.gridy = 3;
        add(passLabel, gbc);

        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        add(passwordField, gbc);

        // Error Label
        errorLabel = new JLabel(" ");
        errorLabel.setForeground(new Color(255, 85, 85)); // Soft red
        errorLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        add(errorLabel, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setOpaque(false);
        
        loginBtn = new JButton("Login");
        loginBtn.setBackground(new Color(65, 131, 215));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        
        registerBtn = new JButton("Register");
        registerBtn.setBackground(new Color(38, 166, 91));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFocusPainted(false);

        buttonPanel.add(loginBtn);
        buttonPanel.add(registerBtn);

        gbc.gridy = 5;
        add(buttonPanel, gbc);

        // Action Listeners
        loginBtn.addActionListener(this::handleLogin);
        registerBtn.addActionListener(this::handleRegister);
    }

    private void handleLogin(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter username and password.");
            return;
        }

        User user = userDAO.authenticate(username, password);
        if (user != null) {
            userDAO.updateLastLogin(user.getUserId());
            errorLabel.setText(" ");
            mainApp.onLoginSuccess(user);
        } else {
            errorLabel.setText("Invalid credentials. Try again.");
        }
    }

    private void handleRegister(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter username and password to register.");
            return;
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setEmail(username + "@example.com"); // Simple stub
        newUser.setFullName(username);
        newUser.setCurrentLevel(1);
        newUser.setTotalXp(0);
        newUser.setActive(true);
        newUser.setAiPredictionEnabled(true);
        newUser.setAnomalyDetectionEnabled(true);
        newUser.setAdaptiveDifficultyEnabled(true);

        boolean success = userDAO.registerUser(newUser);
        if (success) {
            errorLabel.setForeground(new Color(38, 166, 91)); // Green
            errorLabel.setText("Registration successful! You can now log in.");
        } else {
            errorLabel.setForeground(new Color(255, 85, 85)); // Red
            errorLabel.setText("Registration failed. Username may exist.");
        }
    }
}
