package com.habit.ai;

import com.habit.dao.HabitLogDAO;
import com.habit.model.HabitLog;

import java.util.List;


public class LSTMPredictor {

    private HabitLogDAO logDAO;

    public LSTMPredictor() {
        this.logDAO = new HabitLogDAO();
    }

    
    public float predictSuccessProbability(long habitId, int currentStreak, double difficultyMultiplier) {
        List<HabitLog> logs = logDAO.getLogsByHabitId(habitId);
        
        if (logs == null || logs.isEmpty()) {
            return 50.0f; // 
        }

        
        double decayFactor = 0.85; 
        
        
        double streakFactor = Math.min(1.0, currentStreak / 21.0); 
        double difficultyAdjustment = 1.0 / Math.max(0.5, difficultyMultiplier); 

        probability = (streakFactor * 60.0) + 20.0; 
        probability *= difficultyAdjustment;


        if (probability > 99.9) probability = 99.9;
        if (probability < 5.0) probability = 5.0;

        return (float) probability;
    }
}
