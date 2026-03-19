package com.habit.ui;

import com.habit.ai.AutoencoderDetector;
import com.habit.ai.LSTMPredictor;
import com.habit.model.Habit;
import com.habit.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AnalyticsPanel extends JPanel {

    private User currentUser;
    private Habit selectedHabit;
    
    private AutoencoderDetector anomalyDetector;
    private LSTMPredictor lstmPredictor;

    public AnalyticsPanel(User currentUser, Habit selectedHabit) {
        this.currentUser = currentUser;
        this.selectedHabit = selectedHabit;
        this.anomalyDetector = new AutoencoderDetector();
        this.lstmPredictor = new LSTMPredictor();

        setLayout(new BorderLayout());
        setBackground(new Color(40, 44, 52));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // Header
        JLabel titleLabel = new JLabel("AI Insights & Analytics", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(241, 196, 15)); // Gold
        add(titleLabel, BorderLayout.NORTH);

        // Grid for Stats
        JPanel statsGrid = new JPanel(new GridLayout(3, 1, 10, 10));
        statsGrid.setOpaque(false);
        statsGrid.setBorder(new EmptyBorder(20, 0, 0, 0));

        // 1. LSTM Success Prediction
        JPanel lstmPanel = createStatCard("LSTM Success Prediction (Tomorrow)", getLSTMPredictionString());
        statsGrid.add(lstmPanel);

        // 2. Behavioral Anomaly
        JPanel anomalyPanel = createStatCard("Autoencoder Behavioral Anomaly", getAnomalyString());
        statsGrid.add(anomalyPanel);
        
        // 3. Simulated Heatmap
        JPanel heatmapPanel = createHeatmapCard();
        statsGrid.add(heatmapPanel);

        add(statsGrid, BorderLayout.CENTER);
    }

    private JPanel createStatCard(String title, String value) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(50, 54, 62));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 74, 82), 1),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setForeground(Color.LIGHT_GRAY);
        titleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel valueLbl = new JLabel(value);
        valueLbl.setForeground(Color.WHITE);
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 18));

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valueLbl, BorderLayout.CENTER);

        return card;
    }
    
    private JPanel createHeatmapCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(50, 54, 62));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 74, 82), 1),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel titleLbl = new JLabel("Failure Heatmap (Last 7 Days)");
        titleLbl.setForeground(Color.LIGHT_GRAY);
        titleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        card.add(titleLbl, BorderLayout.NORTH);

        JPanel maps = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        maps.setOpaque(false);
        
        // Draw 7 colored boxes simulating a heatmap
        for (int i=0; i<7; i++) {
            JPanel dayBox = new JPanel();
            dayBox.setPreferredSize(new Dimension(25, 25));
            // Simulate random completion vs failure
            boolean completed = Math.random() > 0.3;
            if (completed) {
                dayBox.setBackground(new Color(38, 166, 91)); // Green
                dayBox.setToolTipText("Completed");
            } else {
                dayBox.setBackground(new Color(255, 85, 85)); // Red
                dayBox.setToolTipText("Failed/Missed");
            }
            maps.add(dayBox);
        }
        
        card.add(maps, BorderLayout.CENTER);
        return card;
    }

    private String getLSTMPredictionString() {
        if (!currentUser.isAiPredictionEnabled()) return "DISABLED by user.";
        if (selectedHabit == null) return "Select a habit to view prediction.";
        
        float prob = lstmPredictor.predictSuccessProbability(
            selectedHabit.getHabitId(), 
            selectedHabit.getCurrentStreak(), 
            selectedHabit.getCurrentDifficultyMultiplier() > 0 ? selectedHabit.getCurrentDifficultyMultiplier() : 1.0f
        );
        return String.format("%.1f%% Probability of Success", prob);
    }

    private String getAnomalyString() {
        if (!currentUser.isAnomalyDetectionEnabled()) return "DISABLED by user.";
        float score = anomalyDetector.calculateAnomalyScore(currentUser.getUserId());
        String severity = anomalyDetector.classifySeverity(score);
        
        return String.format("Score: %.1f/100 (Severity: %s)", score, severity);
    }
}
