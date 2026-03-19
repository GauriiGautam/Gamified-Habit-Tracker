package com.habit.ai;

import com.habit.dao.HabitLogDAO;
import com.habit.model.HabitLog;

import java.sql.Time;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HabitDNAAnalyzer {
    
    private HabitLogDAO logDAO;

    public HabitDNAAnalyzer() {
        this.logDAO = new HabitLogDAO();
    }

    
    public String calculateOptimalTimeOfDay(long habitId) {
        List<HabitLog> logs = logDAO.getLogsByHabitId(habitId);
        if (logs == null || logs.isEmpty()) {
            return "Insufficient Data";
        }

        Map<String, Integer> timeBuckets = new HashMap<>();
        timeBuckets.put("Morning", 0);
        timeBuckets.put("Afternoon", 0);
        timeBuckets.put("Evening", 0);
        timeBuckets.put("Night", 0);

        for (HabitLog log : logs) {
            Time completionTime = log.getCompletionTime();
            if (completionTime == null) continue;
            
            @SuppressWarnings("deprecation")
            int hour = completionTime.getHours();

            if (hour >= 5 && hour < 12) {
                timeBuckets.put("Morning", timeBuckets.get("Morning") + 1);
            } else if (hour >= 12 && hour < 17) {
                timeBuckets.put("Afternoon", timeBuckets.get("Afternoon") + 1);
            } else if (hour >= 17 && hour < 21) {
                timeBuckets.put("Evening", timeBuckets.get("Evening") + 1);
            } else {
                timeBuckets.put("Night", timeBuckets.get("Night") + 1);
            }
        }

        
        String optimalTime = "Morning";
        int maxCompletions = -1;

        for (Map.Entry<String, Integer> entry : timeBuckets.entrySet()) {
            if (entry.getValue() > maxCompletions) {
                maxCompletions = entry.getValue();
                optimalTime = entry.getKey();
            }
        }

        return optimalTime;
    }

    
    public float calculateConsistencyScore(long habitId) {
        List<HabitLog> logs = logDAO.getLogsByHabitId(habitId);
        if (logs == null || logs.size() < 3) {
            return 0.0f; 
        }

        
        double sumHour = 0;
        for (HabitLog log : logs) {
            @SuppressWarnings("deprecation")
            int hour = log.getCompletionTime().getHours();
            sumHour += hour;
        }

        double meanHour = sumHour / logs.size();
        double varianceSum = 0;

        for (HabitLog log : logs) {
            @SuppressWarnings("deprecation")
            int hour = log.getCompletionTime().getHours();
            varianceSum += Math.pow(hour - meanHour, 2);
        }

        double standardDeviation = Math.sqrt(varianceSum / logs.size());
        
        
        float consistencyScore = (float) (100.0 - (standardDeviation * (100.0 / 6.0)));
        if (consistencyScore < 0) consistencyScore = 0;
        if (consistencyScore > 100) consistencyScore = 100;

        return consistencyScore;
    }
}
