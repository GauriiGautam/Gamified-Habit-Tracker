package com.habit.exceptions;

public class InvalidAuthenticationException extends HabitException {
    public InvalidAuthenticationException(String message) {
        super(message);
    }

    public InvalidAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
