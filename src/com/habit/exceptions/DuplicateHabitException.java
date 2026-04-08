package com.habit.exceptions;

public class DuplicateHabitException extends HabitException {
    public DuplicateHabitException(String message) {
        super(message);
    }

    public DuplicateHabitException(String message, Throwable cause) {
        super(message, cause);
    }
}
