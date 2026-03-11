package com.example.ProjectManagementSystem.dto.taskDtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

import jakarta.validation.constraints.FutureOrPresent;

@Data
public class CreateTaskRequest {
    @NotBlank(message = "Task title should not be blank")
    @Size(max=100,message = "Task's title length should not exceed 100")
    private String title;
    @FutureOrPresent(message = "Due date cannot be in the past")
    private LocalDate dueDate;
    private Long assignedUserId;
}

