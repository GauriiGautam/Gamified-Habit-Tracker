package com.habit.service;

import com.habit.dao.HabitLogDAO;
import com.habit.model.HabitLog;

import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FailureHeatmapService {

    private HabitLogDAO logDAO;

    public FailureHeatmapService() {
        this.logDAO = new HabitLogDAO();
    }

    /**
     * Generates a Map of Dates to Status Strings for a 30-day window.
     * "COMPLETED" - Green
     * "MISSED" - Red
     */
    public Map<LocalDate, String> generateMonthlyHeatmap(long habitId, LocalDate startDate, LocalDate endDate) {
        List<HabitLog> logs = logDAO.getLogsByHabitId(habitId);
        
        // Map to hold log existence check quickly
        Map<LocalDate, Boolean> completionMap = new HashMap<>();
        if (logs != null) {
            for (HabitLog log : logs) {
                Date sqlDate = log.getCompletionDate();
                if (sqlDate != null) {
                    completionMap.put(sqlDate.toLocalDate(), true);
                }
            }
        }

        Map<LocalDate, String> heatmap = new HashMap<>();
        
        // Calculate days between
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        
        for (int i = 0; i <= daysBetween; i++) {
            LocalDate currentDate = startDate.plusDays(i);
            
            // If the date is in the future, we don't map it.
            if (currentDate.isAfter(LocalDate.now())) {
                continue;
            }

            if (completionMap.containsKey(currentDate)) {
                heatmap.put(currentDate, "COMPLETED"); // Will be colored Green in UI
            } else {
                heatmap.put(currentDate, "MISSED"); // Will be colored Red in UI
            }
        }

        return heatmap;
    }
}
