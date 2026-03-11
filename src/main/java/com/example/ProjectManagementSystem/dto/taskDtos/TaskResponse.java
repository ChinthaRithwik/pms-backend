package com.example.ProjectManagementSystem.dto.taskDtos;

import com.example.ProjectManagementSystem.entity.enums.TaskStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
public class TaskResponse {

    private Long id;

    private String title;

    private TaskStatus status;

    private LocalDate dueDate;

    private Long projectId;

    private Long assignedUserId;

    private String assignedUserName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}