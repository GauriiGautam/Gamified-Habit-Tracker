package com.habit.ai;

import com.habit.dao.HabitLogDAO;
import com.habit.model.HabitLog;

import java.util.List;


public class MoodCorrelationAnalyzer {
    
    private HabitLogDAO logDAO;

    public MoodCorrelationAnalyzer() {
        this.logDAO = new HabitLogDAO();
    }

    
    public float calculateDifficultyMoodCorrelation(long habitId) {
        List<HabitLog> logs = logDAO.getLogsByHabitId(habitId);
        if (logs == null || logs.size() < 5) return 0.0f; 

        int n = 0;
        double sumX = 0, sumY = 0, sumXY = 0;
        double sumX2 = 0, sumY2 = 0;

       
        for (HabitLog log : logs) {
            if (log.getMoodAtCompletion() > 0) { 
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
        
        
        if (denominatorX == 0 || denominatorY == 0) return 0.0f;

        double denominator = Math.sqrt(denominatorX * denominatorY);
        return (float) (numerator / denominator);
    }
}
