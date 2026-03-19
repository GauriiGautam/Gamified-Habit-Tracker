package com.habit.ai;

import com.habit.dao.HabitLogDAO;
import com.habit.model.HabitLog;

import java.util.List;

/**
 * Mood-Habit Correlation Module
 * Computes statistical correlations between user mood (1-5 scale) and habit consistency.
 */
public class MoodCorrelationAnalyzer {
    
    private HabitLogDAO logDAO;

    public MoodCorrelationAnalyzer() {
        this.logDAO = new HabitLogDAO();
    }

    /**
     * Calculates the Pearson Correlation Coefficient (r) between Difficulty and Mood for a habit.
     * Returns a float between -1.0 and 1.0.
     * A high positive value indicates that harder tasks result in better moods 
     * (or better moods result in completing harder tasks).
     * A negative value indicates the opposite.
     */
    public float calculateDifficultyMoodCorrelation(long habitId) {
        List<HabitLog> logs = logDAO.getLogsByHabitId(habitId);
        if (logs == null || logs.size() < 5) return 0.0f; // Require min dataset size

        int n = 0;
        double sumX = 0, sumY = 0, sumXY = 0;
        double sumX2 = 0, sumY2 = 0;

        // X = Difficulty (Multiplier), Y = Mood Score (1-5)
        for (HabitLog log : logs) {
            if (log.getMoodAtCompletion() > 0) { // Only log entries where mood was recorded
                double x = log.getDifficultyAtCompletion();
                double y = log.getMoodAtCompletion();
                
                sumX += x;
                sumY += y;
                sumXY += x * y;
                sumX2 += x * x;
                sumY2 += y * y;
                n++;
            }
        }

        if (n < 5) return 0.0f;

        double numerator = (n * sumXY) - (sumX * sumY);
        double denominatorX = (n * sumX2) - (sumX * sumX);
        double denominatorY = (n * sumY2) - (sumY * sumY);
        
        // Defend against division by zero (e.g., if there is no variance at all)
        if (denominatorX == 0 || denominatorY == 0) return 0.0f;

        double denominator = Math.sqrt(denominatorX * denominatorY);
        return (float) (numerator / denominator);
    }
}
