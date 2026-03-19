package com.habit.model;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

public class HabitLog {
    private long logId;
    private long habitId;
    private long userId;
    private Date completionDate;
    private Time completionTime;
    private String notes;
    private int xpAwarded;
    private int moodAtCompletion;
    private float difficultyAtCompletion;
    private boolean isEdited;
    private Timestamp createdTimestamp;

    public HabitLog() {}

    public HabitLog(long logId, long habitId, long userId, Date completionDate, Time completionTime, String notes, int xpAwarded, int moodAtCompletion, float difficultyAtCompletion, boolean isEdited, Timestamp createdTimestamp) {
        this.logId = logId;
        this.habitId = habitId;
        this.userId = userId;
        this.completionDate = completionDate;
        this.completionTime = completionTime;
        this.notes = notes;
        this.xpAwarded = xpAwarded;
        this.moodAtCompletion = moodAtCompletion;
        this.difficultyAtCompletion = difficultyAtCompletion;
        this.isEdited = isEdited;
        this.createdTimestamp = createdTimestamp;
    }

    // Getters and Setters
    public long getLogId() { return logId; }
    public void setLogId(long logId) { this.logId = logId; }

    public long getHabitId() { return habitId; }
    public void setHabitId(long habitId) { this.habitId = habitId; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public Date getCompletionDate() { return completionDate; }
    public void setCompletionDate(Date completionDate) { this.completionDate = completionDate; }

    public Time getCompletionTime() { return completionTime; }
    public void setCompletionTime(Time completionTime) { this.completionTime = completionTime; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public int getXpAwarded() { return xpAwarded; }
    public void setXpAwarded(int xpAwarded) { this.xpAwarded = xpAwarded; }

    public int getMoodAtCompletion() { return moodAtCompletion; }
    public void setMoodAtCompletion(int moodAtCompletion) { this.moodAtCompletion = moodAtCompletion; }

    public float getDifficultyAtCompletion() { return difficultyAtCompletion; }
    public void setDifficultyAtCompletion(float difficultyAtCompletion) { this.difficultyAtCompletion = difficultyAtCompletion; }

    public boolean isEdited() { return isEdited; }
    public void setEdited(boolean edited) { isEdited = edited; }

    public Timestamp getCreatedTimestamp() { return createdTimestamp; }
    public void setCreatedTimestamp(Timestamp createdTimestamp) { this.createdTimestamp = createdTimestamp; }
}
