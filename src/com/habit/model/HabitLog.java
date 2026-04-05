package com.habit.model;

public class HabitLog {

    private int logId;
    private int habitId;
    private int userId;
    private String completionDate;
    private String completionTime;
    private String notes;
    private int xpAwarded;

    public HabitLog() {
    }

    public HabitLog(int logId, int habitId, int userId, String completionDate,
            String completionTime, String notes, int xpAwarded) {
        this.logId = logId;
        this.habitId = habitId;
        this.userId = userId;
        this.completionDate = completionDate;
        this.completionTime = completionTime;
        this.notes = notes;
        this.xpAwarded = xpAwarded;
    }

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public int getHabitId() {
        return habitId;
    }

    public void setHabitId(int habitId) {
        this.habitId = habitId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(String completionDate) {
        this.completionDate = completionDate;
    }

    public String getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(String completionTime) {
        this.completionTime = completionTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getXpAwarded() {
        return xpAwarded;
    }

    public void setXpAwarded(int xpAwarded) {
        this.xpAwarded = xpAwarded;
    }

    @Override
    public String toString() {
        return "HabitLog[" + logId + " | HabitID: " + habitId + " | Date: " + completionDate + " | XP: " + xpAwarded
                + "]";
    }
}