package com.habit.exceptions;

public class HabitException extends Exception {

    private static final long serialVersionUID = 1L;
    private int errorCode;

    public HabitException(String message) {
        super(message);
        this.errorCode = 0;
    }

    public HabitException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public HabitException(String message, Throwable cause) {
        super(message, cause);
    }

    public int getErrorCode() {
        return errorCode;
    }
}