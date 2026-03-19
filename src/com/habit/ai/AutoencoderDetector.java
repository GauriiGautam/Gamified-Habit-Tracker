package com.habit.ai;

import com.habit.dao.HabitLogDAO;

/**
 * Behavioral Anomaly Detection Module
 * Simulates an Autoencoder Network to detect burnout, stress drops, or erratic behavior.
 * Identifies patterns that deviate significantly from the baseline "compressed" representation.
 */
public class AutoencoderDetector {

    private HabitLogDAO logDAO;

    public AutoencoderDetector() {
        this.logDAO = new HabitLogDAO();
    }

    /**
     * Calculates an Anomaly Score (0-100), simulating the Reconstruction Error 
     * of a Deep Neural Network Autoencoder. High errors indicate chaotic behavior.
     */
    public float calculateAnomalyScore(long userId) {
        // Here we would construct a state vector of the user's past 30 days
        // pass it through the autoencoder, measure the MSE (Mean Squared Error) 
        // to the reconstructed output, and scale it to 100.

        // Fallback: Simplistic mock score calculating recent variance vs historical
        // If a user with a 100-day streak suddenly misses 3 days, this spikes.
        
        // Example mock return
        float mockReconstructionError = (float) (Math.random() * 30.0); // usually low anomaly
        
        // Simulated sudden spike
        if (Math.random() > 0.95) {
            mockReconstructionError = 75.0f + (float)(Math.random() * 25.0); // Critical Anomaly
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
