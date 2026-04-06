package com.habit.gui;

import com.habit.dao.HabitDAO;
import com.habit.db.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class LogHabitFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private int userId;
    private DashboardFrame dashboard;
    private HabitDAO dao;
    private JComboBox<String> habitCombo;
    private JTextField dateField;
    private JTextArea notesArea;
    private JButton logButton;
    private JButton cancelButton;
    private JLabel xpHintLabel;

    public LogHabitFrame(int userId, DashboardFrame dashboard) {
        this.userId    = userId;
        this.dashboard = dashboard;
        this.dao       = new HabitDAO();
        initUI();
        loadHabits();
    }

    private void initUI() {
        setTitle("Log Habit Completion");
        setSize(460, 480);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(18, 18, 28));
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        // ── Title ─────────────────────────────────────────────────
        JLabel titleLabel = new JLabel("✅  LOG HABIT COMPLETION");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(99, 102, 241));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;

        // ── Habit select ──────────────────────────────────────────
        gbc.gridy = 1; gbc.gridx = 0;
        mainPanel.add(createLabel("Select Habit:"), gbc);

        habitCombo = new JComboBox<>();
        habitCombo.setBackground(new Color(30, 30, 50));
        habitCombo.setForeground(Color.WHITE);
        habitCombo.setFont(new Font("Arial", Font.PLAIN, 13));
        habitCombo.setPreferredSize(new Dimension(210, 36));
        gbc.gridx = 1;
        mainPanel.add(habitCombo, gbc);

        // XP hint label — updates when habit selection changes
        xpHintLabel = new JLabel(" ");
        xpHintLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        xpHintLabel.setForeground(new Color(99, 180, 99));
        xpHintLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 2; gbc.gridx = 0; gbc.gridwidth = 2;
        mainPanel.add(xpHintLabel, gbc);
        gbc.gridwidth = 1;

        habitCombo.addActionListener(e -> updateXpHint());

        // ── Date ──────────────────────────────────────────────────
        gbc.gridy = 3; gbc.gridx = 0;
        mainPanel.add(createLabel("Date:"), gbc);

        dateField = new JTextField(LocalDate.now().toString());
        dateField.setPreferredSize(new Dimension(210, 36));
        dateField.setBackground(new Color(30, 30, 50));
        dateField.setForeground(Color.WHITE);
        dateField.setCaretColor(Color.WHITE);
        dateField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(99, 102, 241), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        dateField.setFont(new Font("Arial", Font.PLAIN, 13));
        dateField.setToolTipText("Format: YYYY-MM-DD (today's date is pre-filled)");
        gbc.gridx = 1;
        mainPanel.add(dateField, gbc);

        JLabel dateHint = new JLabel("Format: YYYY-MM-DD  (today is pre-filled)");
        dateHint.setFont(new Font("Arial", Font.ITALIC, 11));
        dateHint.setForeground(new Color(150, 150, 180));
        dateHint.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 2;
        mainPanel.add(dateHint, gbc);
        gbc.gridwidth = 1;

        // ── Notes ─────────────────────────────────────────────────
        gbc.gridy = 5; gbc.gridx = 0;
        mainPanel.add(createLabel("Notes (optional):"), gbc);

        notesArea = new JTextArea(4, 15);
        notesArea.setBackground(new Color(30, 30, 50));
        notesArea.setForeground(Color.WHITE);
        notesArea.setCaretColor(Color.WHITE);
        notesArea.setFont(new Font("Arial", Font.PLAIN, 13));
        notesArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(99, 102, 241), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JScrollPane notesScroll = new JScrollPane(notesArea);
        gbc.gridx = 1;
        mainPanel.add(notesScroll, gbc);

        // ── Buttons ───────────────────────────────────────────────
        logButton = new JButton("✅  LOG HABIT");
        logButton.setPreferredSize(new Dimension(180, 44));
        logButton.setBackground(new Color(40, 167, 69));
        logButton.setForeground(Color.WHITE);
        logButton.setFont(new Font("Arial", Font.BOLD, 14));
        logButton.setBorderPainted(false);
        logButton.setFocusPainted(false);
        logButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(120, 44));
        cancelButton.setBackground(new Color(60, 60, 80));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFont(new Font("Arial", Font.BOLD, 14));
        cancelButton.setBorderPainted(false);
        cancelButton.setFocusPainted(false);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnPanel.setBackground(new Color(18, 18, 28));
        btnPanel.add(logButton);
        btnPanel.add(cancelButton);

        gbc.gridy = 6; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 15, 10, 15);
        mainPanel.add(btnPanel, gbc);

        logButton.addActionListener(e -> handleLogHabit());
        cancelButton.addActionListener(e -> dispose());

        add(mainPanel);
    }

    private void loadHabits() {
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pst = conn.prepareStatement(
                "SELECT HabitID, HabitName, XPValue FROM HABIT WHERE UserID = ? AND IsActive = 'Active'");
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            boolean found = false;
            while (rs.next()) {
                found = true;
                habitCombo.addItem(rs.getInt("HabitID") + " — " + rs.getString("HabitName")
                        + "  (+" + rs.getInt("XPValue") + " XP)");
            }
            if (!found) {
                habitCombo.addItem("No active habits found");
                logButton.setEnabled(false);
            } else {
                updateXpHint();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading habits: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateXpHint() {
        String item = (String) habitCombo.getSelectedItem();
        if (item == null) return;
        // Extract XP from format "ID — Name  (+N XP)"
        try {
            int start = item.lastIndexOf("(+") + 2;
            int end   = item.lastIndexOf(" XP)");
            if (start > 1 && end > start) {
                int xp = Integer.parseInt(item.substring(start, end).trim());
                xpHintLabel.setText("🎯 Completing this habit will earn you +" + xp + " XP!");
            }
        } catch (NumberFormatException ignored) { xpHintLabel.setText(" "); }
    }

    private void handleLogHabit() {
        String habitStr = (String) habitCombo.getSelectedItem();
        if (habitStr == null || habitStr.startsWith("No active")) {
            JOptionPane.showMessageDialog(this,
                "Please add a habit first before logging.", "No Habits", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Parse habit ID
        int habitId;
        try {
            habitId = Integer.parseInt(habitStr.split(" — ")[0].trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Could not parse habit ID.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate date
        String date = dateField.getText().trim();
        try {
            LocalDate parsed = LocalDate.parse(date);
            if (parsed.isAfter(LocalDate.now())) {
                JOptionPane.showMessageDialog(this,
                    "You cannot log a habit for a future date.", "Invalid Date", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                "Invalid date format. Please use YYYY-MM-DD.", "Invalid Date", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String notes = notesArea.getText().trim();
        if (notes.isEmpty()) notes = null;

        dao.logHabit(habitId, userId, date, notes);

        JOptionPane.showMessageDialog(this,
            "<html><b>Habit logged! 🎉</b><br>XP has been added to your account.</html>",
            "Logged!", JOptionPane.INFORMATION_MESSAGE);

        dashboard.refreshHabits();   // refreshes table + XP header
        dispose();
    }

    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", Font.BOLD, 13));
        l.setForeground(Color.WHITE);
        return l;
    }
}