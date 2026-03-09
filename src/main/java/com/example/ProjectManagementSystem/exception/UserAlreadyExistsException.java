package com.example.ProjectManagementSystem.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String msg) { super(msg); }
}
