package com.habit.interfaces;

import com.habit.model.User;
import com.habit.exceptions.UserNotFoundException;
import com.habit.exceptions.InvalidAuthenticationException;

public interface UserOperations {
    User registerUser(String username, String password, String email, String fullName, String bio);
    User loginUser(String username, String password) throws InvalidAuthenticationException;
    void updateProfile(int userId, String newEmail, String newPassword) throws UserNotFoundException;
    void deleteUser(int userId) throws UserNotFoundException;
}
