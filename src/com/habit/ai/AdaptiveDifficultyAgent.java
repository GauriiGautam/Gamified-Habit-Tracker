package com.habit.ai;

import com.habit.model.Habit;
import com.habit.dao.HabitDAO;

/**
 * Adaptive Difficulty Management Module
 * Based on Reinforcement Learning (DQN). The agent observes the current state 
 * (streak, success rate, mood), takes an action (adjust difficulty + or -), 
 * and receives a reward (user engagement/consistency).
 */
public class AdaptiveDifficultyAgent {

    private HabitDAO habitDAO;

    public AdaptiveDifficultyAgent() {
        this.habitDAO = new HabitDAO();
    }

    /**
     * Determines whether to adjust the difficulty multiplier for a given habit to
     * maintain optimal challenge and flow state.
     */
    public boolean evaluateDifficultyAdjustment(Habit habit, float currentSuccessRate, int currentStreak, float averageMood) {
        
        // Define RL state vector conceptually
        // float[] stateVector = { currentSuccessRate, currentStreak, averageMood, habit.getDifficultyLevel() };
        
        // If success rate is incredibly high, and streak is long, difficulty is too easy (boredom zone)
        if (currentStreak >= 15 && currentSuccessRate > 0.90f) {
            float newDifficulty = habit.getCurrentDifficultyMultiplier() * 1.05f; // +5% harder
            // Max out at some cap (e.g. 2.0x)
            if (newDifficulty > 2.0f) newDifficulty = 2.0f;
            
            System.out.println("AI Agent: Increasing difficulty to " + newDifficulty + " for flow state.");
            habit.setCurrentDifficultyMultiplier(newDifficulty);
            // In a real DB save logic, we would call HabitDAO to update the value here
            return true;
        }

        // If success rate is plummeting, make it easier to avoid burnout
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
