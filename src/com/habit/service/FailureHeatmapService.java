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


    public Map<LocalDate, String> generateMonthlyHeatmap(long habitId, LocalDate startDate, LocalDate endDate) {
        List<HabitLog> logs = logDAO.getLogsByHabitId(habitId);
        

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
        
       
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        
        for (int i = 0; i <= daysBetween; i++) {
            LocalDate currentDate = startDate.plusDays(i);
            
            
            if (currentDate.isAfter(LocalDate.now())) {
                continue;
            }

            if (completionMap.containsKey(currentDate)) {
                heatmap.put(currentDate, "COMPLETED");
            } else {
                heatmap.put(currentDate, "MISSED"); 
            }
        }

        return heatmap;
    }
}
