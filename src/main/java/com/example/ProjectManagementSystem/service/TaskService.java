package com.example.ProjectManagementSystem.service;

import com.example.ProjectManagementSystem.dto.taskDtos.*;
import com.example.ProjectManagementSystem.entity.enums.TaskStatus;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public interface TaskService {
    TaskResponse createNewTask(@Valid CreateTaskRequest request, Long id);

    Page<TaskResponse> getAllTasks(Long id, TaskStatus status, Pageable pageable);

    TaskResponse getTaskById(Long taskId);

    TaskResponse updateTask(UpdateTaskRequest request, Long id);

    TaskResponse updateTaskStatus(Long taskId, UpdateTaskStatusRequest request);

    void deleteTaskById(Long taskId);

    TaskResponse updateTasksAssignedUser(Long taskId, @Valid UpdateAssignedUserRequest request);

    Page<TaskResponse> getOverDueTasks(Pageable pageable);

    Page<TaskResponse> searchTasks(Long projectId, TaskStatus status, Long assignedUserId, LocalDate dueBefore, Pageable pageable);
}
