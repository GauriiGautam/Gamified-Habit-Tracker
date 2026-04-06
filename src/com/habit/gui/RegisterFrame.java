package com.habit.gui;

import com.habit.dao.HabitDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class RegisterFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    // User ID is now auto-generated — not shown to the user
    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField fullNameField;
    private JTextField bioField;
    private JButton registerButton;
    private JButton backButton;
    private HabitDAO dao;

    public RegisterFrame() {
        dao = new HabitDAO();
        initUI();
    }

    private void initUI() {
        setTitle("Habit Tracker - Create Account");
        setSize(500, 660);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(18, 18, 28));
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ── Title ──────────────────────────────────────────────────
        JLabel titleLabel = new JLabel("CREATE ACCOUNT");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(new Color(99, 102, 241));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        JLabel subtitleLabel = new JLabel("Start your habit journey today!");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(150, 150, 180));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        mainPanel.add(subtitleLabel, gbc);

        gbc.gridwidth = 1;

        // ── Full Name ───────────────────────────────────────────────
        gbc.gridy = 2; gbc.gridx = 0;
        mainPanel.add(createLabel("Full Name: *"), gbc);
        fullNameField = createTextField();
        fullNameField.setToolTipText("Enter your full name");
        gbc.gridx = 1;
        mainPanel.add(fullNameField, gbc);

        // ── Username ────────────────────────────────────────────────
        gbc.gridy = 3; gbc.gridx = 0;
        mainPanel.add(createLabel("Username: *"), gbc);
        usernameField = createTextField();
        usernameField.setToolTipText("Choose a unique username (no spaces)");
        gbc.gridx = 1;
        mainPanel.add(usernameField, gbc);

        // ── Email ───────────────────────────────────────────────────
        gbc.gridy = 4; gbc.gridx = 0;
        mainPanel.add(createLabel("Email: *"), gbc);
        emailField = createTextField();
        emailField.setToolTipText("Enter a valid email address");
        gbc.gridx = 1;
        mainPanel.add(emailField, gbc);

        // ── Password ────────────────────────────────────────────────
        gbc.gridy = 5; gbc.gridx = 0;
        mainPanel.add(createLabel("Password: *"), gbc);
        passwordField = new JPasswordField(15);
        stylePasswordField(passwordField);
        passwordField.setToolTipText("Minimum 6 characters");
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);

        // ── Confirm Password ────────────────────────────────────────
        gbc.gridy = 6; gbc.gridx = 0;
        mainPanel.add(createLabel("Confirm Password: *"), gbc);
        confirmPasswordField = new JPasswordField(15);
        stylePasswordField(confirmPasswordField);
        confirmPasswordField.setToolTipText("Re-enter your password");
        gbc.gridx = 1;
        mainPanel.add(confirmPasswordField, gbc);

        // ── Bio (optional) ──────────────────────────────────────────
        gbc.gridy = 7; gbc.gridx = 0;
        mainPanel.add(createLabel("Bio (optional):"), gbc);
        bioField = createTextField();
        bioField.setToolTipText("A short description about yourself");
        gbc.gridx = 1;
        mainPanel.add(bioField, gbc);

        // ── Required note ───────────────────────────────────────────
        JLabel requiredNote = new JLabel("* Required fields");
        requiredNote.setFont(new Font("Arial", Font.ITALIC, 11));
        requiredNote.setForeground(new Color(150, 150, 180));
        requiredNote.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 8; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 10, 4, 10);
        mainPanel.add(requiredNote, gbc);

        // ── Register Button ─────────────────────────────────────────
        registerButton = new JButton("CREATE MY ACCOUNT");
        registerButton.setPreferredSize(new Dimension(320, 46));
        registerButton.setBackground(new Color(99, 102, 241));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFont(new Font("Arial", Font.BOLD, 15));
        registerButton.setBorderPainted(false);
        registerButton.setFocusPainted(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 9; gbc.gridwidth = 2;
        gbc.insets = new Insets(12, 10, 5, 10);
        mainPanel.add(registerButton, gbc);

        // ── Back Button ─────────────────────────────────────────────
        backButton = new JButton("Already have an account? Login");
        backButton.setBackground(new Color(18, 18, 28));
        backButton.setForeground(new Color(99, 102, 241));
        backButton.setFont(new Font("Arial", Font.PLAIN, 12));
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 10;
        gbc.insets = new Insets(2, 10, 10, 10);
        mainPanel.add(backButton, gbc);

        // ── Listeners ───────────────────────────────────────────────
        registerButton.addActionListener(e -> handleRegister());
        backButton.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        // Allow Enter key to submit from confirm password field
        confirmPasswordField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) handleRegister();
            }
        });

        // Hover effect on register button
        registerButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                registerButton.setBackground(new Color(79, 82, 221));
            }
            public void mouseExited(MouseEvent e) {
                registerButton.setBackground(new Color(99, 102, 241));
            }
        });

        add(mainPanel);
    }

    private void handleRegister() {
        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String email    = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String confirmPwd = new String(confirmPasswordField.getPassword()).trim();
        String bio      = bioField.getText().trim();

        // ── Validation ──────────────────────────────────────────────
        if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showError("Please fill all required fields (*).");
            return;
        }

        if (username.contains(" ")) {
            showError("Username cannot contain spaces.");
            return;
        }

        if (username.length() < 3) {
            showError("Username must be at least 3 characters.");
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            showError("Please enter a valid email address.");
            return;
        }

        if (password.length() < 6) {
            showError("Password must be at least 6 characters.");
            return;
        }

        if (!password.equals(confirmPwd)) {
            showError("Passwords do not match. Please try again.");
            confirmPasswordField.setText("");
            confirmPasswordField.requestFocus();
            return;
        }

        // ── Auto-generate User ID ────────────────────────────────────
        int userId = dao.getNextUserId();

        // ── Register ─────────────────────────────────────────────────
        // All new users start at Level 1 (Beginner)
        dao.addUser(userId, username, email, password, fullName, bio, 1);

        JOptionPane.showMessageDialog(this,
                "<html><b>Welcome, " + fullName + "!</b><br>"
                + "Your account has been created.<br>"
                + "You can now log in with your username and password.</html>",
                "Account Created!", JOptionPane.INFORMATION_MESSAGE);

        new LoginFrame().setVisible(true);
        dispose();
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Registration Error", JOptionPane.ERROR_MESSAGE);
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
        field.setPreferredSize(new Dimension(210, 36));
        field.setBackground(new Color(30, 30, 50));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(99, 102, 241), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        field.setFont(new Font("Arial", Font.PLAIN, 13));
    }

    private void stylePasswordField(JPasswordField field) {
        field.setPreferredSize(new Dimension(210, 36));
        field.setBackground(new Color(30, 30, 50));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(99, 102, 241), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        field.setFont(new Font("Arial", Font.PLAIN, 13));
    }
}