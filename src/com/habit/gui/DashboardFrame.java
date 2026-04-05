package com.habit.gui;

import com.habit.dao.HabitDAO;
import com.habit.db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class DashboardFrame extends JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private int userId;
    private String fullName;
    private HabitDAO dao;
    private JTable habitTable;
    private DefaultTableModel tableModel;
    private JLabel welcomeLabel;
    private JLabel xpLabel;
    private JPanel contentPanel;

    public DashboardFrame(int userId, String fullName) {
        this.userId = userId;
        this.fullName = fullName;
        this.dao = new HabitDAO();
        initUI();
        loadHabits();
        loadUserStats();
    }

    private void initUI() {
        setTitle("Habit Tracker - Dashboard");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(18, 18, 28));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(25, 25, 40));
        topPanel.setPreferredSize(new Dimension(1000, 70));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        welcomeLabel = new JLabel("Welcome, " + fullName + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setForeground(Color.WHITE);

        xpLabel = new JLabel("XP: Loading...");
        xpLabel.setFont(new Font("Arial", Font.BOLD, 14));
        xpLabel.setForeground(new Color(99, 102, 241));

        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(220, 53, 69));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFont(new Font("Arial", Font.BOLD, 12));
        logoutButton.setBorderPainted(false);
        logoutButton.setFocusPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(new Color(25, 25, 40));
        rightPanel.add(xpLabel);
        rightPanel.add(logoutButton);

        topPanel.add(welcomeLabel, BorderLayout.WEST);
        topPanel.add(rightPanel, BorderLayout.EAST);

        JPanel sidePanel = new JPanel();
        sidePanel.setBackground(new Color(25, 25, 40));
        sidePanel.setPreferredSize(new Dimension(200, 650));
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JLabel menuLabel = new JLabel("MENU");
        menuLabel.setFont(new Font("Arial", Font.BOLD, 14));
        menuLabel.setForeground(new Color(99, 102, 241));
        menuLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidePanel.add(menuLabel);
        sidePanel.add(Box.createVerticalStrut(20));

        String[] menuItems = {
                "My Habits",
                "Add Habit",
                "Log Habit",
                "Habit Logs",
                "Statistics",
                "Badges",
                "Leaderboard",
                "Mood Entries"
        };

        for (String item : menuItems) {
            JButton btn = createMenuButton(item);
            sidePanel.add(btn);
            sidePanel.add(Box.createVerticalStrut(10));
        }

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(18, 18, 28));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] columns = { "Habit ID", "Habit Name", "Category", "Frequency", "Streak", "Completions", "XP" };
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        habitTable = new JTable(tableModel);
        habitTable.setBackground(new Color(30, 30, 50));
        habitTable.setForeground(Color.WHITE);
        habitTable.setFont(new Font("Arial", Font.PLAIN, 13));
        habitTable.setRowHeight(35);
        habitTable.setSelectionBackground(new Color(99, 102, 241));
        habitTable.setGridColor(new Color(50, 50, 70));

        JTableHeader header = habitTable.getTableHeader();
        header.setBackground(new Color(99, 102, 241));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 13));

        JScrollPane scrollPane = new JScrollPane(habitTable);
        scrollPane.setBackground(new Color(18, 18, 28));
        scrollPane.getViewport().setBackground(new Color(30, 30, 50));

        JLabel tableTitle = new JLabel("MY HABITS");
        tableTitle.setFont(new Font("Arial", Font.BOLD, 18));
        tableTitle.setForeground(Color.WHITE);
        tableTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(new Color(18, 18, 28));

        JButton refreshBtn = createActionButton("Refresh", new Color(99, 102, 241));
        JButton deleteBtn = createActionButton("Delete Selected", new Color(220, 53, 69));

        buttonPanel.add(refreshBtn);
        buttonPanel.add(deleteBtn);

        contentPanel.add(tableTitle, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(sidePanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to logout?",
                        "Logout", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    new LoginFrame().setVisible(true);
                    dispose();
                }
            }
        });

        refreshBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadHabits();
            }
        });

        deleteBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleDeleteHabit();
            }
        });

        add(mainPanel);
    }

    private void loadHabits() {
        tableModel.setRowCount(0);
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT h.HabitID, h.HabitName, c.CategoryName, h.Frequency, " +
                    "h.CurrentStreak, h.TotalCompletions, h.XPValue " +
                    "FROM HABIT h JOIN CATEGORY c ON h.CategoryID = c.CategoryID " +
                    "WHERE h.UserID = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[] {
                        rs.getInt("HabitID"),
                        rs.getString("HabitName"),
                        rs.getString("CategoryName"),
                        rs.getString("Frequency"),
                        rs.getInt("CurrentStreak"),
                        rs.getInt("TotalCompletions"),
                        rs.getInt("XPValue")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading habits: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadUserStats() {
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT TotalXP, CurrentLevel FROM USER WHERE UserID = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                int xp = rs.getInt("TotalXP");
                int level = rs.getInt("CurrentLevel");
                xpLabel.setText("Level " + level + "  |  XP: " + xp);
            }
        } catch (SQLException e) {
            System.err.println("Error loading stats: " + e.getMessage());
        }
    }

    private void handleDeleteHabit() {
        int selectedRow = habitTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a habit to delete!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int habitId = (int) tableModel.getValueAt(selectedRow, 0);
        String habitName = (String) tableModel.getValueAt(selectedRow, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete habit: " + habitName + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dao.deleteHabit(habitId);
            loadHabits();
            JOptionPane.showMessageDialog(this,
                    "Habit deleted successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(180, 40));
        btn.setPreferredSize(new Dimension(180, 40));
        btn.setBackground(new Color(35, 35, 55));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.PLAIN, 13));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);

        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleMenuClick(text);
            }
        });

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(99, 102, 241));
            }

            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(35, 35, 55));
            }
        });

        return btn;
    }

    private JButton createActionButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void handleMenuClick(String menu) {
        switch (menu) {
            case "My Habits":
                loadHabits();
                break;
            case "Add Habit":
                new AddHabitFrame(userId, this).setVisible(true);
                break;
            case "Log Habit":
                new LogHabitFrame(userId, this).setVisible(true);
                break;
            case "Habit Logs":
                showLogsPanel();
                break;
            case "Statistics":
                showStatsPanel();
                break;
            case "Badges":
                showBadgesPanel();
                break;
            case "Leaderboard":
                showLeaderboardPanel();
                break;
            case "Mood Entries":
                showMoodPanel();
                break;
        }
    }

    private void showLogsPanel() {
        tableModel.setRowCount(0);
        habitTable.setModel(new DefaultTableModel(
                new String[] { "Log ID", "Habit Name", "Date", "Time", "Notes", "XP" }, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        });
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT l.LogID, h.HabitName, l.CompletionDate, " +
                    "l.CompletionTime, l.Notes, l.XPAwarded " +
                    "FROM HABIT_LOG l JOIN HABIT h ON l.HabitID = h.HabitID " +
                    "WHERE l.UserID = ? ORDER BY l.CompletionDate DESC";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            DefaultTableModel model = (DefaultTableModel) habitTable.getModel();
            while (rs.next()) {
                String notes = rs.getString("Notes");
                if (notes == null)
                    notes = "-";
                model.addRow(new Object[] {
                        rs.getInt("LogID"),
                        rs.getString("HabitName"),
                        rs.getString("CompletionDate"),
                        rs.getString("CompletionTime"),
                        notes,
                        rs.getInt("XPAwarded")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void showStatsPanel() {
        tableModel.setRowCount(0);
        habitTable.setModel(new DefaultTableModel(
                new String[] { "Habit Name", "Current Streak", "Best Streak", "Completions", "XP/Log" }, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        });
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT h.HabitName, h.CurrentStreak, h.LongestStreak, " +
                    "h.TotalCompletions, h.XPValue " +
                    "FROM HABIT h WHERE h.UserID = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            DefaultTableModel model = (DefaultTableModel) habitTable.getModel();
            while (rs.next()) {
                model.addRow(new Object[] {
                        rs.getString("HabitName"),
                        rs.getInt("CurrentStreak"),
                        rs.getInt("LongestStreak"),
                        rs.getInt("TotalCompletions"),
                        rs.getInt("XPValue")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void showBadgesPanel() {
        habitTable.setModel(new DefaultTableModel(
                new String[] { "Badge Name", "Type", "XP Reward", "Rarity", "Earned Date" }, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        });
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT b.BadgeName, b.BadgeType, b.XPReward, " +
                    "b.RarityLevel, ub.EarnedDate " +
                    "FROM USER_BADGE ub JOIN BADGE b ON ub.BadgeID = b.BadgeID " +
                    "WHERE ub.UserID = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            DefaultTableModel model = (DefaultTableModel) habitTable.getModel();
            while (rs.next()) {
                model.addRow(new Object[] {
                        rs.getString("BadgeName"),
                        rs.getString("BadgeType"),
                        rs.getInt("XPReward"),
                        rs.getString("RarityLevel"),
                        rs.getString("EarnedDate")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void showLeaderboardPanel() {
        habitTable.setModel(new DefaultTableModel(
                new String[] { "Rank", "Username", "Total XP", "Habits Completed", "Period" }, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        });
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT u.Username, l.RankPosition, l.TotalXP, " +
                    "l.HabitsCompleted, l.PeriodType " +
                    "FROM LEADERBOARD l JOIN USER u ON l.UserID = u.UserID " +
                    "WHERE l.PeriodType = 'weekly' ORDER BY l.RankPosition";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            DefaultTableModel model = (DefaultTableModel) habitTable.getModel();
            while (rs.next()) {
                model.addRow(new Object[] {
                        rs.getInt("RankPosition"),
                        rs.getString("Username"),
                        rs.getInt("TotalXP"),
                        rs.getInt("HabitsCompleted"),
                        rs.getString("PeriodType")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void showMoodPanel() {
        habitTable.setModel(new DefaultTableModel(
                new String[] { "Date", "Score", "Mood Type", "Notes" }, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        });
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT MoodDate, MoodScore, MoodType, Notes " +
                    "FROM MOOD_ENTRY WHERE UserID = ? ORDER BY MoodDate DESC";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            DefaultTableModel model = (DefaultTableModel) habitTable.getModel();
            while (rs.next()) {
                String notes = rs.getString("Notes");
                if (notes == null)
                    notes = "-";
                model.addRow(new Object[] {
                        rs.getString("MoodDate"),
                        rs.getInt("MoodScore"),
                        rs.getString("MoodType"),
                        notes
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    public void refreshHabits() {
        loadHabits();
        loadUserStats();
    }
}