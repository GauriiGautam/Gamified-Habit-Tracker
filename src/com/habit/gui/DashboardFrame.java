package com.habit.gui;

import com.habit.dao.HabitDAO;
import com.habit.dao.MoodDAO;
import com.habit.db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;

public class DashboardFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private int userId;
    private String fullName;
    private HabitDAO dao;
    private MoodDAO moodDao;


    private JLabel welcomeLabel;
    private JLabel xpLabel;
    private JLabel levelLabel;


    private JTable dataTable;
    private DefaultTableModel tableModel;


    private JPanel contentPanel;
    private JLabel sectionTitle;
    private JPanel actionPanel;
    private String currentSection = "My Habits";

    public DashboardFrame(int userId, String fullName) {
        this.userId   = userId;
        this.fullName = fullName;
        this.dao      = new HabitDAO();
        this.moodDao  = new MoodDAO();
        initUI();
        showHabitsSection();
        loadUserStats();
    }





    private void initUI() {
        setTitle("Habit Tracker — Dashboard");
        setSize(1050, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(30, 30, 30));

        mainPanel.add(buildTopBar(),  BorderLayout.NORTH);
        mainPanel.add(buildSideNav(), BorderLayout.WEST);


        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(30, 30, 30));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        sectionTitle = new JLabel("MY HABITS");
        sectionTitle.setFont(new Font("Arial", Font.BOLD, 18));
        sectionTitle.setForeground(Color.WHITE);
        sectionTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));

        tableModel = new DefaultTableModel() {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        dataTable = buildStyledTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(dataTable);
        scrollPane.setBackground(new Color(30, 30, 30));
        scrollPane.getViewport().setBackground(new Color(37, 37, 38));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));

        actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actionPanel.setBackground(new Color(30, 30, 30));
        actionPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        contentPanel.add(sectionTitle, BorderLayout.NORTH);
        contentPanel.add(scrollPane,   BorderLayout.CENTER);
        contentPanel.add(actionPanel,  BorderLayout.SOUTH);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel);
    }


    private JPanel buildTopBar() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(new Color(45, 45, 48));
        top.setPreferredSize(new Dimension(1050, 65));
        top.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        welcomeLabel = new JLabel("Welcome, " + fullName + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setForeground(Color.WHITE);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        right.setBackground(new Color(45, 45, 48));

        levelLabel = new JLabel("Level: —");
        levelLabel.setFont(new Font("Arial", Font.BOLD, 13));
        levelLabel.setForeground(new Color(252, 211, 77));

        xpLabel = new JLabel("XP: —");
        xpLabel.setFont(new Font("Arial", Font.BOLD, 13));
        xpLabel.setForeground(new Color(14, 99, 156));

        JButton logoutBtn = makeButton("Logout", new Color(211, 47, 47));
        logoutBtn.addActionListener(e -> {
            int ok = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) {
                new LoginFrame().setVisible(true);
                dispose();
            }
        });

        right.add(levelLabel);
        right.add(xpLabel);
        right.add(logoutBtn);

        top.add(welcomeLabel, BorderLayout.WEST);
        top.add(right,        BorderLayout.EAST);
        return top;
    }


    private JPanel buildSideNav() {
        JPanel side = new JPanel();
        side.setBackground(new Color(45, 45, 48));
        side.setPreferredSize(new Dimension(195, 680));
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBorder(BorderFactory.createEmptyBorder(22, 8, 22, 8));

        JLabel menuTitle = new JLabel("MENU");
        menuTitle.setFont(new Font("Arial", Font.BOLD, 12));
        menuTitle.setForeground(new Color(14, 99, 156));
        menuTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        side.add(menuTitle);
        side.add(Box.createVerticalStrut(16));


        String[] navItems = {
            "My Habits",
            "Add Habit",
            "Log Habit",
            "Habit Logs",
            "Statistics",
            "Badges",
            "Leaderboard",
            "Mood Entries"
        };

        for (String item : navItems) {
            JButton btn = createNavButton(item);
            btn.addActionListener(e -> handleMenuClick(item));
            side.add(btn);
            side.add(Box.createVerticalStrut(8));
        }

        return side;
    }





    private void handleMenuClick(String section) {
        if (section.equals("Add Habit") || section.equals("Log Habit")) {
            if (section.equals("Add Habit")) new AddHabitFrame(userId, this).setVisible(true);
            if (section.equals("Log Habit")) new LogHabitFrame(userId, this).setVisible(true);
            return;
        }
        currentSection = section;
        switch (section) {
            case "My Habits":    showHabitsSection();       break;
            case "Habit Logs":   showLogsSection();         break;
            case "Statistics":   showStatsSection();        break;
            case "Badges":       showBadgesSection();       break;
            case "Leaderboard":  showLeaderboardSection();  break;
            case "Mood Entries": showMoodSection();         break;
        }
    }





    private void showHabitsSection() {
        sectionTitle.setText("MY HABITS");
        resetTable(new String[]{"ID", "Habit Name", "Category", "Frequency", "Streak", "Completions", "XP"});
        resetRenderer();

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
        } catch (SQLException e) { showDbError(e); }

        JTextField searchField = new JTextField(15);
        searchField.setBackground(new Color(37, 37, 38));
        searchField.setForeground(Color.WHITE);
        searchField.setCaretColor(Color.WHITE);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60)), 
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        dataTable.setRowSorter(sorter);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
            private void filter() {
                String text = searchField.getText();
                if (text.trim().length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        JLabel searchLabel = new JLabel("Search Habit: ");
        searchLabel.setForeground(Color.WHITE);

        setActionPanel(
            makeButton("Refresh",        new Color(85, 85, 85),   ev -> showHabitsSection()),
            makeButton("Update Habit",   new Color(14, 99, 156), ev -> handleUpdateHabit()),
            makeButton("Delete Habit",   new Color(211, 47, 47),  ev -> handleDeleteHabit()),
            Box.createHorizontalStrut(20),
            searchLabel,
            searchField
        );
    }

    private void handleUpdateHabit() {
        int row = dataTable.getSelectedRow();
        if (row < 0) { showSelectRowWarning(); return; }
        int habitId = (int) tableModel.getValueAt(row, 0);
        new AddHabitFrame(userId, this, habitId).setVisible(true);
    }

    private void handleDeleteHabit() {
        int row = dataTable.getSelectedRow();
        if (row < 0) { showSelectRowWarning(); return; }
        int    habitId = (int)    tableModel.getValueAt(row, 0);
        String hName   = (String) tableModel.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete habit \"" + hName + "\"?\nThis will also delete all its logs and cannot be undone.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            dao.deleteHabit(habitId);
            showHabitsSection();
            JOptionPane.showMessageDialog(this, "Habit deleted successfully.", "Done",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }





    private void showLogsSection() {
        sectionTitle.setText("HABIT LOGS");
        resetTable(new String[]{"Log ID", "Habit Name", "Date", "Time", "Notes", "XP Earned"});
        resetRenderer();

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
        } catch (SQLException e) { showDbError(e); }

        setActionPanel(makeButton("Refresh", new Color(85, 85, 85), ev -> showLogsSection()));
    }





    private void showStatsSection() {
        sectionTitle.setText("HABIT STATISTICS");
        resetTable(new String[]{"Habit Name", "Current Streak", "Best Streak", "Total Completions", "XP per Log"});
        resetRenderer();

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
        } catch (SQLException e) { showDbError(e); }

        setActionPanel(makeButton("Refresh", new Color(85, 85, 85), ev -> showStatsSection()));
    }





    private void showBadgesSection() {
        sectionTitle.setText("BADGES");
        resetTable(new String[]{"Badge Name", "Type", "Rarity", "XP Reward", "Status", "Earned Date"});

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pst = conn.prepareStatement(
                "SELECT b.BadgeName, b.BadgeType, b.RarityLevel, b.XPReward, ub.EarnedDate " +
                "FROM BADGE b " +
                "LEFT JOIN USER_BADGE ub ON b.BadgeID = ub.BadgeID AND ub.UserID = ? " +
                "WHERE b.IsActive = 1 ORDER BY ub.EarnedDate DESC, b.BadgeID");
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String earnedDate = rs.getString("EarnedDate");
                String status     = (earnedDate != null) ? "EARNED" : "LOCKED";
                String dateStr    = (earnedDate != null) ? earnedDate : "—";
                tableModel.addRow(new Object[]{
                    rs.getString("BadgeName"),
                    rs.getString("BadgeType"),
                    rs.getString("RarityLevel"),
                    rs.getInt("XPReward"),
                    status,
                    dateStr
                });
            }
        } catch (SQLException e) { showDbError(e); }


        dataTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, col);
                String status = (String) table.getModel().getValueAt(row, 4);
                if (isSelected) {
                    c.setBackground(new Color(14, 99, 156));
                } else if ("EARNED".equals(status)) {
                    c.setBackground(new Color(20, 55, 30));
                } else {
                    c.setBackground(new Color(45, 30, 30));
                }
                c.setForeground(Color.WHITE);
                return c;
            }
        });

        setActionPanel(makeButton("Refresh", new Color(85, 85, 85), ev -> showBadgesSection()));
    }





    private void showLeaderboardSection() {
        sectionTitle.setText("LEADERBOARD — ALL TIME");
        resetTable(new String[]{"Rank", "Username", "Level", "Total XP", "Habits Logged"});

        final String myUsername = getMyUsername();

        try {
            Connection conn = DBConnection.getConnection();

            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(
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

                String rankStr = rank == 1 ? "#1 (Top)" : "#" + rank;
                tableModel.addRow(new Object[]{
                    rankStr,
                    rs.getString("Username"),
                    rs.getString("LevelName"),
                    rs.getInt("TotalXP"),
                    rs.getInt("HabitsCompleted")
                });
                rank++;
            }
        } catch (SQLException e) { showDbError(e); }


        dataTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, col);
                String uname = String.valueOf(table.getModel().getValueAt(row, 1));
                if (isSelected) {
                    c.setBackground(new Color(14, 99, 156));
                } else if (myUsername != null && myUsername.equals(uname)) {
                    c.setBackground(new Color(9, 71, 113));
                } else {
                    c.setBackground(new Color(37, 37, 38));
                }
                c.setForeground(Color.WHITE);
                setHorizontalAlignment(col == 0 ? CENTER : LEFT);
                return c;
            }
        });

        setActionPanel(makeButton("Refresh", new Color(85, 85, 85), ev -> showLeaderboardSection()));
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





    private void showMoodSection() {
        sectionTitle.setText("MY MOOD JOURNAL");

        resetTable(new String[]{"MoodID", "Date", "Score", "Mood Type", "Notes"});


        dataTable.getColumnModel().getColumn(0).setMinWidth(0);
        dataTable.getColumnModel().getColumn(0).setMaxWidth(0);
        dataTable.getColumnModel().getColumn(0).setPreferredWidth(0);

        loadMoodData();


        dataTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, col);
                if (isSelected) {
                    c.setBackground(new Color(14, 99, 156));
                } else {
                    Object scoreObj = table.getModel().getValueAt(row, 2);
                    int score = (scoreObj instanceof Integer) ? (Integer) scoreObj : 3;
                    if (score >= 4)      c.setBackground(new Color(20, 55, 30));
                    else if (score <= 2) c.setBackground(new Color(55, 20, 25));
                    else                 c.setBackground(new Color(37, 37, 38));
                }
                c.setForeground(Color.WHITE);
                return c;
            }
        });

        setActionPanel(
            makeButton("Add Today's Mood", new Color(52, 168, 83),  ev -> showAddMoodDialog()),
            makeButton("Edit Selected",    new Color(14, 99, 156), ev -> showEditMoodDialog()),
            makeButton("Delete Selected",  new Color(211, 47, 47),  ev -> handleDeleteMood()),
            makeButton("Refresh",          new Color(85, 85, 85),   ev -> loadMoodData())
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
        } catch (SQLException e) { showDbError(e); }
    }

    private void showAddMoodDialog() {
        JDialog dlg = new JDialog(this, "Add Today's Mood", true);
        dlg.setSize(420, 370);
        dlg.setLocationRelativeTo(this);
        dlg.setResizable(false);

        JPanel p = buildDialogPanel();

        JLabel title = new JLabel("How are you feeling today?");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(new Color(14, 99, 156));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 10, 8, 10);
        gc.fill   = GridBagConstraints.HORIZONTAL;
        gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 2;
        p.add(title, gc);

        gc.gridwidth = 1;
        gc.gridy = 1; gc.gridx = 0;
        p.add(makeDlgLabel("Mood Score (1=low, 5=great):"), gc);

        JSlider slider = new JSlider(1, 5, 3);
        slider.setMajorTickSpacing(1); slider.setPaintTicks(true); slider.setPaintLabels(true);
        slider.setSnapToTicks(true);
        slider.setBackground(new Color(30, 30, 30)); slider.setForeground(Color.WHITE);
        gc.gridx = 1;
        p.add(slider, gc);


        String[] moodHints = {"Very Low", "Low", "Okay", "Good", "Great"};
        JLabel moodHint = new JLabel("Okay");
        moodHint.setForeground(new Color(150, 200, 150));
        moodHint.setFont(new Font("Arial", Font.BOLD, 13));
        moodHint.setHorizontalAlignment(SwingConstants.CENTER);
        slider.addChangeListener(e -> moodHint.setText(moodHints[slider.getValue() - 1]));
        gc.gridwidth = 2; gc.gridx = 0; gc.gridy = 2;
        p.add(moodHint, gc);
        gc.gridwidth = 1;

        gc.gridy = 3; gc.gridx = 0;
        p.add(makeDlgLabel("Mood Type:"), gc);
        String[] moodTypes = {"Happy", "Calm", "Neutral", "Stressed", "Sad", "Excited", "Anxious", "Grateful"};
        JComboBox<String> moodCombo = new JComboBox<>(moodTypes);
        styleCombo(moodCombo);
        gc.gridx = 1;
        p.add(moodCombo, gc);

        gc.gridy = 4; gc.gridx = 0;
        p.add(makeDlgLabel("Notes (optional):"), gc);
        JTextField notesField = new JTextField();
        styleDialogField(notesField);
        gc.gridx = 1;
        p.add(notesField, gc);

        JButton saveBtn   = makeButton("Save",   new Color(52, 168, 83));
        JButton cancelBtn = makeButton("Cancel", new Color(100, 100, 120));
        JPanel btnRow = buildBtnRow(saveBtn, cancelBtn);
        gc.gridy = 5; gc.gridx = 0; gc.gridwidth = 2;
        gc.insets = new Insets(14, 10, 10, 10);
        p.add(btnRow, gc);

        saveBtn.addActionListener(e -> {
            boolean added = moodDao.addMoodEntry(userId, slider.getValue(),
                    (String) moodCombo.getSelectedItem(), notesField.getText().trim());
            if (added) {
                JOptionPane.showMessageDialog(dlg, "Mood saved!", "Saved",
                        JOptionPane.INFORMATION_MESSAGE);
                dlg.dispose();
                loadMoodData();
            } else {
                JOptionPane.showMessageDialog(dlg,
                        "You have already logged your mood for today.\nSelect a row and use Edit to change it.",
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

        int    moodId   = (int)    tableModel.getValueAt(row, 0);
        int    curScore = (int)    tableModel.getValueAt(row, 2);
        String curType  = (String) tableModel.getValueAt(row, 3);
        String curNotes = (String) tableModel.getValueAt(row, 4);
        if ("—".equals(curNotes)) curNotes = "";

        JDialog dlg = new JDialog(this, "Edit Mood Entry", true);
        dlg.setSize(420, 340);
        dlg.setLocationRelativeTo(this);
        dlg.setResizable(false);

        JPanel p = buildDialogPanel();
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 10, 8, 10);
        gc.fill   = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Edit Mood Entry");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(new Color(14, 99, 156));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 2;
        p.add(title, gc);

        gc.gridwidth = 1;
        gc.gridy = 1; gc.gridx = 0;
        p.add(makeDlgLabel("Mood Score (1=low, 5=great):"), gc);
        JSlider slider = new JSlider(1, 5, curScore);
        slider.setMajorTickSpacing(1); slider.setPaintTicks(true); slider.setPaintLabels(true);
        slider.setSnapToTicks(true);
        slider.setBackground(new Color(30, 30, 30)); slider.setForeground(Color.WHITE);
        gc.gridx = 1;
        p.add(slider, gc);

        gc.gridy = 2; gc.gridx = 0;
        p.add(makeDlgLabel("Mood Type:"), gc);
        String[] moodTypes = {"Happy", "Calm", "Neutral", "Stressed", "Sad", "Excited", "Anxious", "Grateful"};
        JComboBox<String> moodCombo = new JComboBox<>(moodTypes);
        moodCombo.setSelectedItem(curType);
        styleCombo(moodCombo);
        gc.gridx = 1;
        p.add(moodCombo, gc);

        gc.gridy = 3; gc.gridx = 0;
        p.add(makeDlgLabel("Notes:"), gc);
        JTextField notesField = new JTextField(curNotes);
        styleDialogField(notesField);
        gc.gridx = 1;
        p.add(notesField, gc);

        JButton saveBtn   = makeButton("Update", new Color(14, 99, 156));
        JButton cancelBtn = makeButton("Cancel", new Color(100, 100, 120));
        JPanel btnRow = buildBtnRow(saveBtn, cancelBtn);
        gc.gridy = 4; gc.gridx = 0; gc.gridwidth = 2;
        gc.insets = new Insets(14, 10, 10, 10);
        p.add(btnRow, gc);

        saveBtn.addActionListener(e -> {
            boolean ok = moodDao.updateMoodEntry(moodId, userId, slider.getValue(),
                    (String) moodCombo.getSelectedItem(), notesField.getText().trim());
            if (ok) {
                JOptionPane.showMessageDialog(dlg, "Mood updated!", "Updated",
                        JOptionPane.INFORMATION_MESSAGE);
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
        int    moodId = (int)    tableModel.getValueAt(row, 0);
        String date   = (String) tableModel.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete mood entry for " + date + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            if (moodDao.deleteMoodEntry(moodId, userId)) {
                loadMoodData();
            } else {
                JOptionPane.showMessageDialog(this, "Could not delete entry.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }





    public void loadUserStats() {
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pst = conn.prepareStatement(
                "SELECT u.TotalXP, u.CurrentLevel, l.LevelName FROM USER u " +
                "JOIN LEVEL l ON u.CurrentLevel = l.LevelID WHERE u.UserID = ?");
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                xpLabel.setText("XP: " + rs.getInt("TotalXP"));
                levelLabel.setText("Level " + rs.getInt("CurrentLevel") + " (" + rs.getString("LevelName") + ")  |  ");
            }
        } catch (SQLException e) {
            System.err.println("Error loading stats: " + e.getMessage());
        }
    }


    public void refreshHabits() {
        handleMenuClick(currentSection);
        loadUserStats();
    }






    private void resetTable(String[] columns) {
        tableModel.setColumnCount(0);
        tableModel.setRowCount(0);
        for (String col : columns) tableModel.addColumn(col);
    }


    private void resetRenderer() {
        dataTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            { setForeground(Color.WHITE); setBackground(new Color(37, 37, 38)); }
        });
    }

    private void setActionPanel(Component... components) {
        actionPanel.removeAll();
        for (Component c : components) actionPanel.add(c);
        actionPanel.revalidate();
        actionPanel.repaint();
    }

    private JTable buildStyledTable(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setBackground(new Color(37, 37, 38));
        t.setForeground(Color.WHITE);
        t.setFont(new Font("Arial", Font.PLAIN, 13));
        t.setRowHeight(34);
        t.setSelectionBackground(new Color(14, 99, 156));
        t.setSelectionForeground(Color.WHITE);
        t.setGridColor(new Color(60, 60, 60));
        t.setShowGrid(true);
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            { setForeground(Color.WHITE); setBackground(new Color(37, 37, 38)); }
        });

        JTableHeader header = t.getTableHeader();
        header.setBackground(new Color(0, 122, 204));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 13));
        header.setReorderingAllowed(false);

        return t;
    }

    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(180, 42));
        btn.setPreferredSize(new Dimension(180, 42));
        btn.setBackground(new Color(62, 62, 66));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.PLAIN, 13));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 0));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(14, 99, 156)); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(new Color(62, 62, 66)); }
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

    private JButton makeButton(String text, Color bg, ActionListener al) {
        JButton btn = makeButton(text, bg);
        btn.addActionListener(al);
        return btn;
    }

    private JPanel buildDialogPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(new Color(30, 30, 30));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        return p;
    }

    private JPanel buildBtnRow(JButton... buttons) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        row.setBackground(new Color(30, 30, 30));
        for (JButton b : buttons) row.add(b);
        return row;
    }

    private JLabel makeDlgLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", Font.BOLD, 13));
        l.setForeground(Color.WHITE);
        return l;
    }

    private void styleDialogField(JTextField f) {
        f.setPreferredSize(new Dimension(190, 34));
        f.setBackground(new Color(37, 37, 38));
        f.setForeground(Color.WHITE);
        f.setCaretColor(Color.WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(14, 99, 156), 1),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        f.setFont(new Font("Arial", Font.PLAIN, 13));
    }

    private void styleCombo(JComboBox<String> c) {
        c.setBackground(new Color(37, 37, 38));
        c.setForeground(Color.WHITE);
        c.setFont(new Font("Arial", Font.PLAIN, 13));
        c.setPreferredSize(new Dimension(190, 34));
    }

    private void showSelectRowWarning() {
        JOptionPane.showMessageDialog(this,
            "Please select a row in the table first.", "Nothing Selected",
            JOptionPane.WARNING_MESSAGE);
    }

    private void showDbError(SQLException e) {
        JOptionPane.showMessageDialog(this,
            "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}