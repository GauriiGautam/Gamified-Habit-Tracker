package com.habit.gui;

import com.habit.dao.HabitDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import com.habit.db.DBConnection;

public class AddHabitFrame extends JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private int userId;
    private DashboardFrame dashboard;
    private HabitDAO dao;
    private JTextField habitNameField;
    private JTextArea descriptionArea;
    private JComboBox<String> categoryCombo;
    private JComboBox<String> frequencyCombo;
    private JComboBox<String> difficultyCombo;
    private JTextField xpField;
    private JButton addButton;
    private JButton cancelButton;

    public AddHabitFrame(int userId, DashboardFrame dashboard) {
        this.userId = userId;
        this.dashboard = dashboard;
        this.dao = new HabitDAO();
        initUI();
        loadCategories();
    }

    private void initUI() {
        setTitle("Add New Habit");
        setSize(480, 580);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(18, 18, 28));
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 15, 8, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("ADD NEW HABIT");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(new Color(99, 102, 241));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;

        gbc.gridy = 1;
        gbc.gridx = 0;
        mainPanel.add(createLabel("Habit Name:"), gbc);
        habitNameField = createTextField();
        gbc.gridx = 1;
        mainPanel.add(habitNameField, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        mainPanel.add(createLabel("Category:"), gbc);
        categoryCombo = new JComboBox<>();
        styleCombo(categoryCombo);
        gbc.gridx = 1;
        mainPanel.add(categoryCombo, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        mainPanel.add(createLabel("Frequency:"), gbc);
        String[] frequencies = { "daily", "weekly", "monthly" };
        frequencyCombo = new JComboBox<>(frequencies);
        styleCombo(frequencyCombo);
        gbc.gridx = 1;
        mainPanel.add(frequencyCombo, gbc);

        gbc.gridy = 4;
        gbc.gridx = 0;
        mainPanel.add(createLabel("Difficulty (1-5):"), gbc);
        String[] difficulties = { "1 - Very Easy", "2 - Easy", "3 - Medium", "4 - Hard", "5 - Very Hard" };
        difficultyCombo = new JComboBox<>(difficulties);
        styleCombo(difficultyCombo);
        gbc.gridx = 1;
        mainPanel.add(difficultyCombo, gbc);

        gbc.gridy = 5;
        gbc.gridx = 0;
        mainPanel.add(createLabel("XP Value:"), gbc);
        xpField = createTextField();
        xpField.setText("10");
        gbc.gridx = 1;
        mainPanel.add(xpField, gbc);

        gbc.gridy = 6;
        gbc.gridx = 0;
        mainPanel.add(createLabel("Description:"), gbc);
        descriptionArea = new JTextArea(3, 15);
        descriptionArea.setBackground(new Color(30, 30, 50));
        descriptionArea.setForeground(Color.WHITE);
        descriptionArea.setCaretColor(Color.WHITE);
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 13));
        descriptionArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(99, 102, 241), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        gbc.gridx = 1;
        mainPanel.add(descScroll, gbc);

        addButton = new JButton("ADD HABIT");
        addButton.setPreferredSize(new Dimension(200, 42));
        addButton.setBackground(new Color(99, 102, 241));
        addButton.setForeground(Color.WHITE);
        addButton.setFont(new Font("Arial", Font.BOLD, 14));
        addButton.setBorderPainted(false);
        addButton.setFocusPainted(false);
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(200, 42));
        cancelButton.setBackground(new Color(60, 60, 80));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFont(new Font("Arial", Font.BOLD, 14));
        cancelButton.setBorderPainted(false);
        cancelButton.setFocusPainted(false);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(new Color(18, 18, 28));
        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        gbc.gridy = 7;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 15, 10, 15);
        mainPanel.add(buttonPanel, gbc);

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleAddHabit();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        add(mainPanel);
    }

    private void loadCategories() {
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT CategoryID, CategoryName FROM CATEGORY";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                categoryCombo.addItem(rs.getInt("CategoryID") + " - " + rs.getString("CategoryName"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading categories: " + e.getMessage());
        }
    }

    private void handleAddHabit() {
        try {
            String habitName = habitNameField.getText().trim();
            if (habitName.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Habit name cannot be empty!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String categoryStr = (String) categoryCombo.getSelectedItem();
            int categoryId = Integer.parseInt(categoryStr.split(" - ")[0]);
            String frequency = (String) frequencyCombo.getSelectedItem();
            int difficulty = difficultyCombo.getSelectedIndex() + 1;
            int xpValue = Integer.parseInt(xpField.getText().trim());

            dao.addHabit(userId, categoryId, habitName, frequency, difficulty, xpValue);

            JOptionPane.showMessageDialog(this,
                    "Habit '" + habitName + "' added successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            dashboard.refreshHabits();
            dispose();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "XP Value must be a number!",
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
        field.setPreferredSize(new Dimension(200, 35));
        field.setBackground(new Color(30, 30, 50));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(99, 102, 241), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        field.setFont(new Font("Arial", Font.PLAIN, 13));
        return field;
    }

    private void styleCombo(JComboBox<String> combo) {
        combo.setBackground(new Color(30, 30, 50));
        combo.setForeground(Color.WHITE);
        combo.setFont(new Font("Arial", Font.PLAIN, 13));
        combo.setPreferredSize(new Dimension(200, 35));
    }
}