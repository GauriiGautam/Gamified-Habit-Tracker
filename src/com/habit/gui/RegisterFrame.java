package com.habit.gui;

import com.habit.dao.HabitDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class RegisterFrame extends JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private JTextField userIdField;
    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JTextField fullNameField;
    private JTextField bioField;
    private JComboBox<String> levelCombo;
    private JButton registerButton;
    private JButton backButton;
    private HabitDAO dao;

    public RegisterFrame() {
        dao = new HabitDAO();
        initUI();
    }

    private void initUI() {
        setTitle("Habit Tracker - Register");
        setSize(480, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(18, 18, 28));
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("CREATE ACCOUNT");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(99, 102, 241));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        mainPanel.add(createLabel("User ID:"), gbc);
        userIdField = createTextField();
        gbc.gridx = 1;
        mainPanel.add(userIdField, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        mainPanel.add(createLabel("Username:"), gbc);
        usernameField = createTextField();
        gbc.gridx = 1;
        mainPanel.add(usernameField, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        mainPanel.add(createLabel("Email:"), gbc);
        emailField = createTextField();
        gbc.gridx = 1;
        mainPanel.add(emailField, gbc);

        gbc.gridy = 4;
        gbc.gridx = 0;
        mainPanel.add(createLabel("Password:"), gbc);
        passwordField = new JPasswordField(15);
        styleTextField(passwordField);
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);

        gbc.gridy = 5;
        gbc.gridx = 0;
        mainPanel.add(createLabel("Full Name:"), gbc);
        fullNameField = createTextField();
        gbc.gridx = 1;
        mainPanel.add(fullNameField, gbc);

        gbc.gridy = 6;
        gbc.gridx = 0;
        mainPanel.add(createLabel("Bio:"), gbc);
        bioField = createTextField();
        gbc.gridx = 1;
        mainPanel.add(bioField, gbc);

        gbc.gridy = 7;
        gbc.gridx = 0;
        mainPanel.add(createLabel("Starting Level:"), gbc);
        String[] levels = { "1 - Beginner", "2 - Explorer", "3 - Achiever", "4 - Champion", "5 - Legend" };
        levelCombo = new JComboBox<>(levels);
        levelCombo.setBackground(new Color(30, 30, 50));
        levelCombo.setForeground(Color.WHITE);
        levelCombo.setFont(new Font("Arial", Font.PLAIN, 13));
        gbc.gridx = 1;
        mainPanel.add(levelCombo, gbc);

        registerButton = new JButton("REGISTER");
        registerButton.setPreferredSize(new Dimension(300, 45));
        registerButton.setBackground(new Color(99, 102, 241));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFont(new Font("Arial", Font.BOLD, 15));
        registerButton.setBorderPainted(false);
        registerButton.setFocusPainted(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 8;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 10, 5, 10);
        mainPanel.add(registerButton, gbc);

        backButton = new JButton("Back to Login");
        backButton.setBackground(new Color(18, 18, 28));
        backButton.setForeground(new Color(99, 102, 241));
        backButton.setFont(new Font("Arial", Font.PLAIN, 12));
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 9;
        gbc.insets = new Insets(5, 10, 10, 10);
        mainPanel.add(backButton, gbc);

        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleRegister();
            }
        });

        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new LoginFrame().setVisible(true);
                dispose();
            }
        });

        add(mainPanel);
    }

    private void handleRegister() {
        try {
            int userId = Integer.parseInt(userIdField.getText().trim());
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String fullName = fullNameField.getText().trim();
            String bio = bioField.getText().trim();
            int level = levelCombo.getSelectedIndex() + 1;

            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please fill all required fields!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!email.contains("@") || !email.contains(".")) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a valid email!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            dao.addUser(userId, username, email, password, fullName, bio, level);
            JOptionPane.showMessageDialog(this,
                    "Account created successfully! Please login.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            new LoginFrame().setVisible(true);
            dispose();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "User ID must be a number!",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        label.setForeground(Color.WHITE);
        return label;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField(15);
        styleTextField(field);
        return field;
    }

    private void styleTextField(JTextField field) {
        field.setPreferredSize(new Dimension(200, 35));
        field.setBackground(new Color(30, 30, 50));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(99, 102, 241), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        field.setFont(new Font("Arial", Font.PLAIN, 13));
    }
}