package com.example.ProjectManagementSystem.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Setter
@Getter
public class ApiErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private Object message;
}
