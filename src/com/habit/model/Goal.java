package com.habit.model;

import java.sql.Date;
import java.sql.Timestamp;

public class Goal {
    private long goalId;
    private long habitId;
    private String goalType;
    private int targetCompletions;
    private Date startDate;
    private Date endDate;
    private String status;
    private int completedCount;
    private Timestamp createdDate;

    public Goal() {}

    public Goal(long goalId, long habitId, String goalType, int targetCompletions, Date startDate, Date endDate, String status, int completedCount, Timestamp createdDate) {
        this.goalId = goalId;
        this.habitId = habitId;
        this.goalType = goalType;
        this.targetCompletions = targetCompletions;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.completedCount = completedCount;
        this.createdDate = createdDate;
    }

    
    public long getGoalId() { return goalId; }
    public void setGoalId(long goalId) { this.goalId = goalId; }

    public long getHabitId() { return habitId; }
    public void setHabitId(long habitId) { this.habitId = habitId; }

    public String getGoalType() { return goalType; }
    public void setGoalType(String goalType) { this.goalType = goalType; }

    public int getTargetCompletions() { return targetCompletions; }
    public void setTargetCompletions(int targetCompletions) { this.targetCompletions = targetCompletions; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getCompletedCount() { return completedCount; }
    public void setCompletedCount(int completedCount) { this.completedCount = completedCount; }

    public Timestamp getCreatedDate() { return createdDate; }
    public void setCreatedDate(Timestamp createdDate) { this.createdDate = createdDate; }
}
