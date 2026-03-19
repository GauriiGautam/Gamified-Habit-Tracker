package com.habit.ai;

import com.habit.dao.HabitLogDAO;


public class AutoencoderDetector {

    private HabitLogDAO logDAO;

    public AutoencoderDetector() {
        this.logDAO = new HabitLogDAO();
    }

    
    public float calculateAnomalyScore(long userId) {
        
        float mockReconstructionError = (float) (Math.random() * 30.0); 
        if (Math.random() > 0.95) {
            mockReconstructionError = 75.0f + (float)(Math.random() * 25.0); 
        }

        return mockReconstructionError;
    }

    public String classifySeverity(float anomalyScore) {
        if (anomalyScore < 30) return "Low";
        else if (anomalyScore < 60) return "Medium";
        else if (anomalyScore < 85) return "High";
        else return "Critical";
    }
}
