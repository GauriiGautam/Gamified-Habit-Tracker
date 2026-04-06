package com.habit.gui;

import com.habit.dao.HabitDAO;
import com.habit.db.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginFrame extends JFrame {




    private static final long serialVersionUID = 1L;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;

    public LoginFrame() {
        new HabitDAO();
        initUI();
    }

    private void initUI() {
        setTitle("Habit Tracker - Login");
        setSize(450, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(30, 30, 30));
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("HABIT TRACKER");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(14, 99, 156));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        JLabel subtitleLabel = new JLabel("Build Better Habits Every Day");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitleLabel.setForeground(new Color(153, 153, 153));
        gbc.gridy = 1;
        mainPanel.add(subtitleLabel, gbc);

        JLabel space = new JLabel(" ");
        gbc.gridy = 2;
        mainPanel.add(space, gbc);

        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 13));
        usernameLabel.setForeground(Color.WHITE);
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(usernameLabel, gbc);

        usernameField = new JTextField(20);
        usernameField.setPreferredSize(new Dimension(300, 40));
        usernameField.setBackground(new Color(37, 37, 38));
        usernameField.setForeground(Color.WHITE);
        usernameField.setCaretColor(Color.WHITE);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(14, 99, 156), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridy = 4;
        mainPanel.add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 13));
        passwordLabel.setForeground(Color.WHITE);
        gbc.gridy = 5;
        mainPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        passwordField.setPreferredSize(new Dimension(300, 40));
        passwordField.setBackground(new Color(37, 37, 38));
        passwordField.setForeground(Color.WHITE);
        passwordField.setCaretColor(Color.WHITE);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(14, 99, 156), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridy = 6;
        mainPanel.add(passwordField, gbc);

        loginButton = new JButton("LOGIN");
        loginButton.setPreferredSize(new Dimension(300, 45));
        loginButton.setBackground(new Color(14, 99, 156));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 15));
        loginButton.setBorderPainted(false);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 7;
        gbc.insets = new Insets(20, 10, 5, 10);
        mainPanel.add(loginButton, gbc);

        registerButton = new JButton("New User? Register Here");
        registerButton.setBackground(new Color(30, 30, 30));
        registerButton.setForeground(new Color(14, 99, 156));
        registerButton.setFont(new Font("Arial", Font.PLAIN, 12));
        registerButton.setBorderPainted(false);
        registerButton.setFocusPainted(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 8;
        gbc.insets = new Insets(5, 10, 10, 10);
        mainPanel.add(registerButton, gbc);

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new RegisterFrame().setVisible(true);
            }
        });

        passwordField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleLogin();
                }
            }
        });

        add(mainPanel);
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter username and password!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT UserID, FullName FROM USER WHERE Username = ? AND Password = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("UserID");
                String fullName = rs.getString("FullName");
                JOptionPane.showMessageDialog(this,
                        "Welcome back, " + fullName + "!",
                        "Login Successful", JOptionPane.INFORMATION_MESSAGE);
                new DashboardFrame(userId, fullName).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Invalid username or password!",
                        "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Database error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LoginFrame().setVisible(true);
            }
        });
    }
}