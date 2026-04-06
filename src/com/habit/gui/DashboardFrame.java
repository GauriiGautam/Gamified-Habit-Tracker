package com.habit.gui;

import com.habit.dao.HabitDAO;
import com.habit.db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class DashboardFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private int userId;
    private String fullName;
    private HabitDAO dao;

    // Top bar
    private JLabel welcomeLabel;
    private JLabel xpLabel;
    private JLabel levelLabel;

    // Central table (reused for every section)
    private JTable dataTable;
    private DefaultTableModel tableModel;

    // Content area
    private JPanel contentPanel;
    private JLabel sectionTitle;
    private JPanel actionPanel;          // south button bar — swapped per section
    private String currentSection = "My Habits";

    // Mood entry MoodID column (hidden, index 0 in mood model)
    // We track it separately so we know which DB row to update/delete.

    public DashboardFrame(int userId, String fullName) {
        this.userId   = userId;
        this.fullName = fullName;
        this.dao      = new HabitDAO();
        initUI();
        showHabitsSection();   // default view
        loadUserStats();
    }

    // ─────────────────────────────────────────────────────────────────
    //  UI SKELETON
    // ─────────────────────────────────────────────────────────────────

    private void initUI() {
        setTitle("Habit Tracker — Dashboard");
        setSize(1050, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        /* ── Main layout ── */
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(18, 18, 28));

        /* ── Top bar ── */
        mainPanel.add(buildTopBar(), BorderLayout.NORTH);

        /* ── Side nav ── */
        mainPanel.add(buildSideNav(), BorderLayout.WEST);

        /* ── Content ── */
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(18, 18, 28));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        sectionTitle = new JLabel("MY HABITS");
        sectionTitle.setFont(new Font("Arial", Font.BOLD, 18));
        sectionTitle.setForeground(Color.WHITE);
        sectionTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));

        /* shared table */
        tableModel = new DefaultTableModel() {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        dataTable = buildStyledTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(dataTable);
        scrollPane.setBackground(new Color(18, 18, 28));
        scrollPane.getViewport().setBackground(new Color(30, 30, 50));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(50, 50, 75)));

        actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actionPanel.setBackground(new Color(18, 18, 28));

        contentPanel.add(sectionTitle, BorderLayout.NORTH);
        contentPanel.add(scrollPane,   BorderLayout.CENTER);
        contentPanel.add(actionPanel,  BorderLayout.SOUTH);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    /* ── Top bar ─────────────────────────────────────────────────── */
    private JPanel buildTopBar() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(new Color(25, 25, 40));
        top.setPreferredSize(new Dimension(1050, 65));
        top.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        welcomeLabel = new JLabel("Welcome, " + fullName + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setForeground(Color.WHITE);

        JPanel rightInfo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        rightInfo.setBackground(new Color(25, 25, 40));

        levelLabel = new JLabel("Level — ");
        levelLabel.setFont(new Font("Arial", Font.BOLD, 13));
        levelLabel.setForeground(new Color(252, 211, 77));

        xpLabel = new JLabel("XP: …");
        xpLabel.setFont(new Font("Arial", Font.BOLD, 13));
        xpLabel.setForeground(new Color(99, 102, 241));

        JButton logoutBtn = makeButton("Logout", new Color(220, 53, 69));
        logoutBtn.addActionListener(e -> {
            int ok = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) {
                new LoginFrame().setVisible(true);
                dispose();
            }
        });

        rightInfo.add(levelLabel);
        rightInfo.add(xpLabel);
        rightInfo.add(logoutBtn);

        top.add(welcomeLabel, BorderLayout.WEST);
        top.add(rightInfo,    BorderLayout.EAST);
        return top;
    }

    /* ── Side nav ────────────────────────────────────────────────── */
    private JPanel buildSideNav() {
        JPanel side = new JPanel();
        side.setBackground(new Color(25, 25, 40));
        side.setPreferredSize(new Dimension(195, 680));
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBorder(BorderFactory.createEmptyBorder(22, 8, 22, 8));

        JLabel menuTitle = new JLabel("MENU");
        menuTitle.setFont(new Font("Arial", Font.BOLD, 12));
        menuTitle.setForeground(new Color(99, 102, 241));
        menuTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        side.add(menuTitle);
        side.add(Box.createVerticalStrut(16));

        String[][] navItems = {
            { "My Habits",    "📋" },
            { "Add Habit",    "➕" },
            { "Log Habit",    "✅" },
            { "Habit Logs",   "📜" },
            { "Statistics",   "📊" },
            { "Badges",       "🏅" },
            { "Leaderboard",  "🏆" },
            { "Mood Entries", "😊" },
        };

        for (String[] item : navItems) {
            JButton btn = createNavButton(item[1] + "  " + item[0]);
            final String section = item[0];
            btn.addActionListener(e -> handleMenuClick(section));
            side.add(btn);
            side.add(Box.createVerticalStrut(8));
        }

        return side;
    }

    // ─────────────────────────────────────────────────────────────────
    //  SECTION HANDLERS
    // ─────────────────────────────────────────────────────────────────

    private void handleMenuClick(String section) {
        currentSection = section;
        switch (section) {
            case "My Habits":   showHabitsSection();      break;
            case "Add Habit":   new AddHabitFrame(userId, this).setVisible(true); break;
            case "Log Habit":   new LogHabitFrame(userId, this).setVisible(true); break;
            case "Habit Logs":  showLogsSection();        break;
            case "Statistics":  showStatsSection();       break;
            case "Badges":      showBadgesSection();      break;
            case "Leaderboard": showLeaderboardSection(); break;
            case "Mood Entries":showMoodSection();        break;
        }
    }

    /* ── MY HABITS ────────────────────────────────────────────────── */
    private void showHabitsSection() {
        sectionTitle.setText("MY HABITS");
        resetTable(new String[]{"ID", "Habit Name", "Category", "Frequency", "Streak 🔥", "Completions", "XP"});

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pst = conn.prepareStatement(
                "SELECT h.HabitID, h.HabitName, c.CategoryName, h.Frequency, " +
                "h.CurrentStreak, h.TotalCompletions, h.XPValue " +
                "FROM HABIT h JOIN CATEGORY c ON h.CategoryID = c.CategoryID " +
                "WHERE h.UserID = ? AND h.IsActive = 'Active' ORDER BY h.HabitID");
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
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
            showDbError(e);
        }

        // Action buttons
        setActionPanel(
            makeButton("🔄 Refresh", new Color(60, 60, 90), e -> showHabitsSection()),
            makeButton("✏️ Rename",  new Color(99, 102, 241), e -> handleRenameHabit()),
            makeButton("🗑️ Delete",  new Color(180, 40, 55),  e -> handleDeleteHabit())
        );
    }

    private void handleRenameHabit() {
        int row = dataTable.getSelectedRow();
        if (row < 0) { showSelectRowWarning(); return; }
        int habitId = (int) tableModel.getValueAt(row, 0);
        String currentName = (String) tableModel.getValueAt(row, 1);
        String newName = JOptionPane.showInputDialog(this,
                "Enter new name for habit:", currentName);
        if (newName != null && !newName.trim().isEmpty()) {
            dao.updateHabit(habitId, newName.trim());
            showHabitsSection();
        }
    }

    private void handleDeleteHabit() {
        int row = dataTable.getSelectedRow();
        if (row < 0) { showSelectRowWarning(); return; }
        int habitId   = (int) tableModel.getValueAt(row, 0);
        String hName  = (String) tableModel.getValueAt(row, 1);
        int confirm   = JOptionPane.showConfirmDialog(this,
                "Delete habit \"" + hName + "\"? This cannot be undone.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            dao.deleteHabit(habitId);
            showHabitsSection();
            JOptionPane.showMessageDialog(this, "Habit deleted.", "Done", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /* ── HABIT LOGS ───────────────────────────────────────────────── */
    private void showLogsSection() {
        sectionTitle.setText("HABIT LOGS");
        resetTable(new String[]{"Log ID", "Habit Name", "Date", "Time", "Notes", "XP Earned"});

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pst = conn.prepareStatement(
                "SELECT l.LogID, h.HabitName, l.CompletionDate, " +
                "l.CompletionTime, l.Notes, l.XPAwarded " +
                "FROM HABIT_LOG l JOIN HABIT h ON l.HabitID = h.HabitID " +
                "WHERE l.UserID = ? ORDER BY l.CompletionDate DESC, l.CompletionTime DESC");
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String notes = rs.getString("Notes");
                if (notes == null) notes = "—";
                tableModel.addRow(new Object[]{
                    rs.getInt("LogID"),
                    rs.getString("HabitName"),
                    rs.getString("CompletionDate"),
                    rs.getString("CompletionTime"),
                    notes,
                    rs.getInt("XPAwarded")
                });
            }
        } catch (SQLException e) {
            showDbError(e);
        }

        setActionPanel(makeButton("🔄 Refresh", new Color(60, 60, 90), e -> showLogsSection()));
    }

    /* ── STATISTICS ───────────────────────────────────────────────── */
    private void showStatsSection() {
        sectionTitle.setText("HABIT STATISTICS");
        resetTable(new String[]{"Habit Name", "Current Streak 🔥", "Best Streak", "Total Completions", "XP per Log"});

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pst = conn.prepareStatement(
                "SELECT h.HabitName, h.CurrentStreak, h.LongestStreak, " +
                "h.TotalCompletions, h.XPValue FROM HABIT h WHERE h.UserID = ?");
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("HabitName"),
                    rs.getInt("CurrentStreak"),
                    rs.getInt("LongestStreak"),
                    rs.getInt("TotalCompletions"),
                    rs.getInt("XPValue")
                });
            }
        } catch (SQLException e) {
            showDbError(e);
        }

        setActionPanel(makeButton("🔄 Refresh", new Color(60, 60, 90), e -> showStatsSection()));
    }

    /* ── BADGES ───────────────────────────────────────────────────── */
    // Shows ALL badges; earned ones are marked ✅ with the date, others are 🔒
    private void showBadgesSection() {
        sectionTitle.setText("BADGES");
        resetTable(new String[]{"Badge Name", "Type", "Rarity", "XP Reward", "Status", "Earned Date"});

        try {
            Connection conn = DBConnection.getConnection();
            // All badges, left-joined with what this user earned
            PreparedStatement pst = conn.prepareStatement(
                "SELECT b.BadgeName, b.BadgeType, b.RarityLevel, b.XPReward, " +
                "b.Description, ub.EarnedDate " +
                "FROM BADGE b " +
                "LEFT JOIN USER_BADGE ub ON b.BadgeID = ub.BadgeID AND ub.UserID = ? " +
                "WHERE b.IsActive = 1 " +
                "ORDER BY ub.EarnedDate DESC, b.BadgeID");
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String earnedDate = rs.getString("EarnedDate");
                String status     = (earnedDate != null) ? "✅ Earned" : "🔒 Locked";
                String dateStr    = (earnedDate != null) ? earnedDate   : "—";
                tableModel.addRow(new Object[]{
                    rs.getString("BadgeName"),
                    rs.getString("BadgeType"),
                    rs.getString("RarityLevel"),
                    rs.getInt("XPReward"),
                    status,
                    dateStr
                });
            }
        } catch (SQLException e) {
            showDbError(e);
        }

        // Colour earned rows green-ish
        dataTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                String status = (String) table.getModel().getValueAt(row, 4);
                if (isSelected) {
                    c.setBackground(new Color(99, 102, 241));
                } else if ("✅ Earned".equals(status)) {
                    c.setBackground(new Color(20, 50, 35));
                } else {
                    c.setBackground(new Color(30, 30, 50));
                }
                c.setForeground(Color.WHITE);
                return c;
            }
        });

        setActionPanel(makeButton("🔄 Refresh", new Color(60, 60, 90), e -> showBadgesSection()));
    }

    /* ── LEADERBOARD ──────────────────────────────────────────────── */
    // Reads LIVE from USER table — every new user appears automatically
    private void showLeaderboardSection() {
        sectionTitle.setText("🏆 LEADERBOARD — ALL TIME");
        resetTable(new String[]{"Rank", "Username", "Level", "Total XP", "Habits Logged"});

        try {
            Connection conn = DBConnection.getConnection();
            // Live query — no dependency on the stale LEADERBOARD table
            Statement st  = conn.createStatement();
            ResultSet rs  = st.executeQuery(
                "SELECT u.UserID, u.Username, l.LevelName, u.TotalXP, " +
                "COUNT(hl.LogID) AS HabitsCompleted " +
                "FROM USER u " +
                "JOIN LEVEL l ON u.CurrentLevel = l.LevelID " +
                "LEFT JOIN HABIT_LOG hl ON u.UserID = hl.UserID " +
                "WHERE u.IsActive = 1 " +
                "GROUP BY u.UserID, u.Username, l.LevelName, u.TotalXP " +
                "ORDER BY u.TotalXP DESC");

            int rank = 1;
            while (rs.next()) {
                String medal = rank == 1 ? "🥇" : rank == 2 ? "🥈" : rank == 3 ? "🥉" : "#" + rank;
                tableModel.addRow(new Object[]{
                    medal,
                    rs.getString("Username"),
                    rs.getString("LevelName"),
                    rs.getInt("TotalXP"),
                    rs.getInt("HabitsCompleted")
                });
                rank++;
            }

            // Highlight current user's row
            final int myId = this.userId;
            // Re-read to find which rank the current user is at
            // Use a renderer based on username match
            final String myUsername = getMyUsername();
            dataTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                        boolean isSelected, boolean hasFocus, int row, int col) {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                    String uname = (String) table.getModel().getValueAt(row, 1);
                    if (isSelected) {
                        c.setBackground(new Color(99, 102, 241));
                    } else if (myUsername != null && myUsername.equals(uname)) {
                        c.setBackground(new Color(40, 50, 90)); // highlight own row
                    } else {
                        c.setBackground(new Color(30, 30, 50));
                    }
                    c.setForeground(Color.WHITE);
                    setHorizontalAlignment(col == 0 ? CENTER : LEFT);
                    return c;
                }
            });

        } catch (SQLException e) {
            showDbError(e);
        }

        setActionPanel(makeButton("🔄 Refresh", new Color(60, 60, 90), e -> showLeaderboardSection()));
    }

    private String getMyUsername() {
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pst = conn.prepareStatement("SELECT Username FROM USER WHERE UserID = ?");
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) return rs.getString("Username");
        } catch (SQLException ignored) {}
        return null;
    }

    /* ── MOOD ENTRIES ─────────────────────────────────────────────── */
    // MoodID stored at column index 0 but hidden via column width
    private void showMoodSection() {
        sectionTitle.setText("😊 MY MOOD JOURNAL");

        // Columns: MoodID(hidden), Date, Score, Mood Type, Notes
        resetTable(new String[]{"MoodID", "Date", "Score", "Mood Type", "Notes"});

        // Hide the MoodID column (col 0) — we still read it programmatically
        dataTable.getColumnModel().getColumn(0).setMinWidth(0);
        dataTable.getColumnModel().getColumn(0).setMaxWidth(0);
        dataTable.getColumnModel().getColumn(0).setPreferredWidth(0);

        loadMoodData();

        // Colour rows by mood score
        dataTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                if (isSelected) {
                    c.setBackground(new Color(99, 102, 241));
                } else {
                    Object scoreObj = table.getModel().getValueAt(row, 2);
                    int score = (scoreObj instanceof Integer) ? (Integer) scoreObj : 3;
                    if (score >= 4)      c.setBackground(new Color(20, 55, 30));
                    else if (score <= 2) c.setBackground(new Color(55, 20, 25));
                    else                 c.setBackground(new Color(30, 30, 50));
                }
                c.setForeground(Color.WHITE);
                return c;
            }
        });

        setActionPanel(
            makeButton("➕ Add Today's Mood",  new Color(40, 167, 69),  e -> showAddMoodDialog()),
            makeButton("✏️ Edit Selected",       new Color(99, 102, 241), e -> showEditMoodDialog()),
            makeButton("🗑️ Delete Selected",     new Color(180, 40, 55),  e -> handleDeleteMood()),
            makeButton("🔄 Refresh",             new Color(60, 60, 90),   e -> { loadMoodData(); })
        );
    }

    private void loadMoodData() {
        tableModel.setRowCount(0);
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pst = conn.prepareStatement(
                "SELECT MoodID, MoodDate, MoodScore, MoodType, Notes " +
                "FROM MOOD_ENTRY WHERE UserID = ? ORDER BY MoodDate DESC");
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String notes = rs.getString("Notes");
                if (notes == null) notes = "—";
                tableModel.addRow(new Object[]{
                    rs.getInt("MoodID"),
                    rs.getString("MoodDate"),
                    rs.getInt("MoodScore"),
                    rs.getString("MoodType"),
                    notes
                });
            }
        } catch (SQLException e) {
            showDbError(e);
        }
    }

    private void showAddMoodDialog() {
        JDialog dlg = new JDialog(this, "Add Today's Mood", true);
        dlg.setSize(400, 380);
        dlg.setLocationRelativeTo(this);
        dlg.setResizable(false);

        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(new Color(18, 18, 28));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 8, 8, 8);
        gc.fill   = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("How are you feeling today?");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(new Color(99, 102, 241));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 2;
        p.add(title, gc);

        gc.gridwidth = 1;

        // Score slider
        gc.gridy = 1; gc.gridx = 0;
        p.add(makeDlgLabel("Mood Score (1–5):"), gc);
        JSlider slider = new JSlider(1, 5, 3);
        slider.setMajorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setSnapToTicks(true);
        slider.setBackground(new Color(18, 18, 28));
        slider.setForeground(Color.WHITE);
        gc.gridx = 1;
        p.add(slider, gc);

        // Emoji hint
        JLabel emojiHint = new JLabel("😐  →  Use slider");
        emojiHint.setForeground(new Color(150, 150, 180));
        emojiHint.setFont(new Font("Arial", Font.PLAIN, 11));
        slider.addChangeListener(e -> {
            String[] emojis = {"😟", "😕", "😐", "🙂", "😄"};
            emojiHint.setText(emojis[slider.getValue() - 1] + "  Score: " + slider.getValue());
        });
        gc.gridwidth = 2; gc.gridx = 0; gc.gridy = 2;
        p.add(emojiHint, gc);
        gc.gridwidth = 1;

        // Mood type dropdown
        gc.gridy = 3; gc.gridx = 0;
        p.add(makeDlgLabel("Mood Type:"), gc);
        String[] moodTypes = {"Happy", "Calm", "Neutral", "Stressed", "Sad", "Excited", "Anxious", "Grateful"};
        JComboBox<String> moodCombo = new JComboBox<>(moodTypes);
        moodCombo.setBackground(new Color(30, 30, 50));
        moodCombo.setForeground(Color.WHITE);
        moodCombo.setFont(new Font("Arial", Font.PLAIN, 13));
        gc.gridx = 1;
        p.add(moodCombo, gc);

        // Notes
        gc.gridy = 4; gc.gridx = 0;
        p.add(makeDlgLabel("Notes (optional):"), gc);
        JTextField notesField = new JTextField();
        styleDialogField(notesField);
        gc.gridx = 1;
        p.add(notesField, gc);

        // Buttons
        JButton saveBtn   = makeButton("Save", new Color(40, 167, 69));
        JButton cancelBtn = makeButton("Cancel", new Color(100, 100, 120));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnRow.setBackground(new Color(18, 18, 28));
        btnRow.add(saveBtn); btnRow.add(cancelBtn);
        gc.gridy = 5; gc.gridx = 0; gc.gridwidth = 2;
        gc.insets = new Insets(14, 8, 8, 8);
        p.add(btnRow, gc);

        saveBtn.addActionListener(e -> {
            int score        = slider.getValue();
            String moodType  = (String) moodCombo.getSelectedItem();
            String notes     = notesField.getText().trim();
            boolean added    = dao.addMoodEntry(userId, score, moodType, notes);
            if (added) {
                JOptionPane.showMessageDialog(dlg, "Mood saved! 🎉", "Saved", JOptionPane.INFORMATION_MESSAGE);
                dlg.dispose();
                loadMoodData();
            } else {
                JOptionPane.showMessageDialog(dlg,
                    "You've already logged your mood for today.\nUse 'Edit Selected' to update it.",
                    "Already Logged", JOptionPane.WARNING_MESSAGE);
            }
        });
        cancelBtn.addActionListener(e -> dlg.dispose());

        dlg.add(p);
        dlg.setVisible(true);
    }

    private void showEditMoodDialog() {
        int row = dataTable.getSelectedRow();
        if (row < 0) { showSelectRowWarning(); return; }

        int    moodId    = (int) tableModel.getValueAt(row, 0);
        int    curScore  = (int) tableModel.getValueAt(row, 2);
        String curType   = (String) tableModel.getValueAt(row, 3);
        String curNotes  = (String) tableModel.getValueAt(row, 4);
        if ("—".equals(curNotes)) curNotes = "";

        JDialog dlg = new JDialog(this, "Edit Mood Entry", true);
        dlg.setSize(400, 350);
        dlg.setLocationRelativeTo(this);
        dlg.setResizable(false);

        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(new Color(18, 18, 28));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 8, 8, 8);
        gc.fill   = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Edit Mood Entry");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(new Color(99, 102, 241));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 2;
        p.add(title, gc);

        gc.gridwidth = 1;

        gc.gridy = 1; gc.gridx = 0;
        p.add(makeDlgLabel("Mood Score (1–5):"), gc);
        JSlider slider = new JSlider(1, 5, curScore);
        slider.setMajorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setSnapToTicks(true);
        slider.setBackground(new Color(18, 18, 28));
        slider.setForeground(Color.WHITE);
        gc.gridx = 1;
        p.add(slider, gc);

        gc.gridy = 2; gc.gridx = 0;
        p.add(makeDlgLabel("Mood Type:"), gc);
        String[] moodTypes = {"Happy", "Calm", "Neutral", "Stressed", "Sad", "Excited", "Anxious", "Grateful"};
        JComboBox<String> moodCombo = new JComboBox<>(moodTypes);
        moodCombo.setSelectedItem(curType);
        moodCombo.setBackground(new Color(30, 30, 50));
        moodCombo.setForeground(Color.WHITE);
        moodCombo.setFont(new Font("Arial", Font.PLAIN, 13));
        gc.gridx = 1;
        p.add(moodCombo, gc);

        gc.gridy = 3; gc.gridx = 0;
        p.add(makeDlgLabel("Notes:"), gc);
        JTextField notesField = new JTextField(curNotes);
        styleDialogField(notesField);
        gc.gridx = 1;
        p.add(notesField, gc);

        JButton saveBtn   = makeButton("Update", new Color(99, 102, 241));
        JButton cancelBtn = makeButton("Cancel", new Color(100, 100, 120));
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnRow.setBackground(new Color(18, 18, 28));
        btnRow.add(saveBtn); btnRow.add(cancelBtn);
        gc.gridy = 4; gc.gridx = 0; gc.gridwidth = 2;
        gc.insets = new Insets(14, 8, 8, 8);
        p.add(btnRow, gc);

        saveBtn.addActionListener(e -> {
            boolean ok = dao.updateMoodEntry(
                moodId, userId, slider.getValue(),
                (String) moodCombo.getSelectedItem(),
                notesField.getText().trim());
            if (ok) {
                JOptionPane.showMessageDialog(dlg, "Mood updated!", "Saved", JOptionPane.INFORMATION_MESSAGE);
                dlg.dispose();
                loadMoodData();
            } else {
                JOptionPane.showMessageDialog(dlg, "Could not update. Please try again.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        cancelBtn.addActionListener(e -> dlg.dispose());

        dlg.add(p);
        dlg.setVisible(true);
    }

    private void handleDeleteMood() {
        int row = dataTable.getSelectedRow();
        if (row < 0) { showSelectRowWarning(); return; }
        int moodId = (int) tableModel.getValueAt(row, 0);
        String date = (String) tableModel.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete mood entry for " + date + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = dao.deleteMoodEntry(moodId, userId);
            if (ok) {
                loadMoodData();
            } else {
                JOptionPane.showMessageDialog(this, "Could not delete entry.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────
    //  USER STATS (top bar XP / level)
    // ─────────────────────────────────────────────────────────────────

    public void loadUserStats() {
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pst = conn.prepareStatement(
                "SELECT u.TotalXP, l.LevelName FROM USER u " +
                "JOIN LEVEL l ON u.CurrentLevel = l.LevelID WHERE u.UserID = ?");
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                xpLabel.setText("XP: " + rs.getInt("TotalXP"));
                levelLabel.setText("⭐ " + rs.getString("LevelName") + "  |  ");
            }
        } catch (SQLException e) {
            System.err.println("Error loading stats: " + e.getMessage());
        }
    }

    /** Called by child frames (AddHabitFrame, LogHabitFrame) after they save. */
    public void refreshHabits() {
        showHabitsSection();
        loadUserStats();   // update XP in the header immediately
    }

    // ─────────────────────────────────────────────────────────────────
    //  HELPERS
    // ─────────────────────────────────────────────────────────────────

    private void resetTable(String[] columns) {
        // Restore default renderer in case a section customised it
        dataTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            { setForeground(Color.WHITE); setBackground(new Color(30, 30, 50)); }
        });
        tableModel.setColumnCount(0);
        tableModel.setRowCount(0);
        for (String col : columns) tableModel.addColumn(col);
    }

    private void setActionPanel(JButton... buttons) {
        actionPanel.removeAll();
        for (JButton btn : buttons) actionPanel.add(btn);
        actionPanel.revalidate();
        actionPanel.repaint();
    }

    private JTable buildStyledTable(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setBackground(new Color(30, 30, 50));
        t.setForeground(Color.WHITE);
        t.setFont(new Font("Arial", Font.PLAIN, 13));
        t.setRowHeight(34);
        t.setSelectionBackground(new Color(99, 102, 241));
        t.setSelectionForeground(Color.WHITE);
        t.setGridColor(new Color(50, 50, 70));
        t.setShowGrid(true);
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            { setForeground(Color.WHITE); setBackground(new Color(30, 30, 50)); }
        });

        JTableHeader header = t.getTableHeader();
        header.setBackground(new Color(60, 63, 150));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 13));
        header.setReorderingAllowed(false);

        return t;
    }

    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(180, 42));
        btn.setPreferredSize(new Dimension(180, 42));
        btn.setBackground(new Color(35, 35, 55));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.PLAIN, 13));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 0));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(99, 102, 241)); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(new Color(35, 35, 55)); }
        });
        return btn;
    }

    private JButton makeButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(7, 14, 7, 14));
        return btn;
    }

    /** Convenience: makeButton with an ActionListener attached. */
    private JButton makeButton(String text, Color bg, ActionListener al) {
        JButton btn = makeButton(text, bg);
        btn.addActionListener(al);
        return btn;
    }

    private JLabel makeDlgLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", Font.BOLD, 13));
        l.setForeground(Color.WHITE);
        return l;
    }

    private void styleDialogField(JTextField f) {
        f.setPreferredSize(new Dimension(180, 34));
        f.setBackground(new Color(30, 30, 50));
        f.setForeground(Color.WHITE);
        f.setCaretColor(Color.WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(99, 102, 241), 1),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        f.setFont(new Font("Arial", Font.PLAIN, 13));
    }

    private void showSelectRowWarning() {
        JOptionPane.showMessageDialog(this,
            "Please select a row in the table first.", "Nothing Selected", JOptionPane.WARNING_MESSAGE);
    }

    private void showDbError(SQLException e) {
        JOptionPane.showMessageDialog(this,
            "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}