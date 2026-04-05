package com.habit.gui;

import com.habit.dao.HabitDAO;
import com.habit.db.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;

public class LogHabitFrame extends JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private int userId;
    private DashboardFrame dashboard;
    private HabitDAO dao;
    private JComboBox<String> habitCombo;
    private JTextField dateField;
    private JTextArea notesArea;
    private JButton logButton;
    private JButton cancelButton;

    public LogHabitFrame(int userId, DashboardFrame dashboard) {
        this.userId = userId;
        this.dashboard = dashboard;
        this.dao = new HabitDAO();
        initUI();
        loadHabits();
    }

    private void initUI() {
        setTitle("Log Habit Completion");
        setSize(450, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(18, 18, 28));
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("LOG HABIT COMPLETION");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(99, 102, 241));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;

        gbc.gridy = 1;
        gbc.gridx = 0;
        mainPanel.add(createLabel("Select Habit:"), gbc);
        habitCombo = new JComboBox<>();
        habitCombo.setBackground(new Color(30, 30, 50));
        habitCombo.setForeground(Color.WHITE);
        habitCombo.setFont(new Font("Arial", Font.PLAIN, 13));
        habitCombo.setPreferredSize(new Dimension(200, 35));
        gbc.gridx = 1;
        mainPanel.add(habitCombo, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        mainPanel.add(createLabel("Date:"), gbc);
        dateField = new JTextField(LocalDate.now().toString());
        dateField.setPreferredSize(new Dimension(200, 35));
        dateField.setBackground(new Color(30, 30, 50));
        dateField.setForeground(Color.WHITE);
        dateField.setCaretColor(Color.WHITE);
        dateField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(99, 102, 241), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        dateField.setFont(new Font("Arial", Font.PLAIN, 13));
        gbc.gridx = 1;
        mainPanel.add(dateField, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        mainPanel.add(createLabel("Notes:"), gbc);
        notesArea = new JTextArea(4, 15);
        notesArea.setBackground(new Color(30, 30, 50));
        notesArea.setForeground(Color.WHITE);
        notesArea.setCaretColor(Color.WHITE);
        notesArea.setFont(new Font("Arial", Font.PLAIN, 13));
        notesArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(99, 102, 241), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        JScrollPane notesScroll = new JScrollPane(notesArea);
        gbc.gridx = 1;
        mainPanel.add(notesScroll, gbc);

        logButton = new JButton("LOG HABIT");
        logButton.setPreferredSize(new Dimension(180, 42));
        logButton.setBackground(new Color(99, 102, 241));
        logButton.setForeground(Color.WHITE);
        logButton.setFont(new Font("Arial", Font.BOLD, 14));
        logButton.setBorderPainted(false);
        logButton.setFocusPainted(false);
        logButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(180, 42));
        cancelButton.setBackground(new Color(60, 60, 80));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFont(new Font("Arial", Font.BOLD, 14));
        cancelButton.setBorderPainted(false);
        cancelButton.setFocusPainted(false);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(new Color(18, 18, 28));
        buttonPanel.add(logButton);
        buttonPanel.add(cancelButton);

        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 15, 10, 15);
        mainPanel.add(buttonPanel, gbc);

        logButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleLogHabit();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        add(mainPanel);
    }

    private void loadHabits() {
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT HabitID, HabitName FROM HABIT WHERE UserID = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                habitCombo.addItem(rs.getInt("HabitID") + " - " + rs.getString("HabitName"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading habits: " + e.getMessage());
        }
    }

    private void handleLogHabit() {
        try {
            String habitStr = (String) habitCombo.getSelectedItem();
            if (habitStr == null) {
                JOptionPane.showMessageDialog(this,
                        "No habits found! Please add a habit first.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int habitId = Integer.parseInt(habitStr.split(" - ")[0]);
            String date = dateField.getText().trim();
            String notes = notesArea.getText().trim();
            if (notes.isEmpty())
                notes = null;

            dao.logHabit(habitId, userId, date, notes);

            JOptionPane.showMessageDialog(this,
                    "Habit logged successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            dashboard.refreshHabits();
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error logging habit: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        label.setForeground(Color.WHITE);
        return label;
    }
}