package com.habit.ai;

import com.habit.model.Habit;
import com.habit.dao.HabitDAO;


public class AdaptiveDifficultyAgent {

    private HabitDAO habitDAO;

    public AdaptiveDifficultyAgent() {
        this.habitDAO = new HabitDAO();
    }

    public boolean evaluateDifficultyAdjustment(Habit habit, float currentSuccessRate, int currentStreak, float averageMood) {
        
        
            float newDifficulty = habit.getCurrentDifficultyMultiplier() * 1.05f; 
            if (newDifficulty > 2.0f) newDifficulty = 2.0f;
            
            System.out.println("AI Agent: Increasing difficulty to " + newDifficulty + " for flow state.");
            habit.setCurrentDifficultyMultiplier(newDifficulty);
            
            return true;
        }

        
        if (currentSuccessRate < 0.40f && habit.getCurrentDifficultyMultiplier() > 0.5f) {
            float newDifficulty = habit.getCurrentDifficultyMultiplier() * 0.90f; // -10% easier
            if (newDifficulty < 0.5f) newDifficulty = 0.5f;
            
            System.out.println("AI Agent: Decreasing difficulty to " + newDifficulty + " to retain engagement.");
            habit.setCurrentDifficultyMultiplier(newDifficulty);
            return true;
        }

        return false;
    }
}
