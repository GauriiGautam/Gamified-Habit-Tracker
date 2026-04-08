package com.habit.exceptions;

public class InsufficientXPException extends HabitException {
    public InsufficientXPException(String message) {
        super(message);
    }

    public InsufficientXPException(String message, Throwable cause) {
        super(message, cause);
    }
}
