package com.example.ProjectManagementSystem.controller;

import com.example.ProjectManagementSystem.dto.taskDtos.*;
import com.example.ProjectManagementSystem.entity.enums.TaskStatus;
import com.example.ProjectManagementSystem.service.TaskService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@SecurityRequirement(name = "BearerAuth")
@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/project/{projectId}")
    public ResponseEntity<TaskResponse> createTask(
            @RequestBody @Valid CreateTaskRequest request,
            @PathVariable Long projectId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.createNewTask(request, projectId));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<Page<TaskResponse>> getTasks(
            @PathVariable Long projectId,
            @Parameter(description = "Filter by task status")
            @RequestParam(required = false) TaskStatus status,
            @PageableDefault(size = 10, sort = "dueDate") Pageable pageable) {
        return ResponseEntity.ok(taskService.getAllTasks(projectId, status, pageable));
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponse> getById(@PathVariable Long taskId) {
        return ResponseEntity.ok(taskService.getTaskById(taskId));
    }

    @GetMapping("/overdue")
    public ResponseEntity<Page<TaskResponse>> getOverDue(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(taskService.getOverDueTasks(pageable));
    }

    @PatchMapping("/{taskId}")
    public ResponseEntity<TaskResponse> update(
            @RequestBody @Valid UpdateTaskRequest request,
            @PathVariable Long taskId) {
        return ResponseEntity.ok(taskService.updateTask(request, taskId));
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<TaskResponse> updateStatus(
            @PathVariable Long taskId,
            @RequestBody UpdateTaskStatusRequest request) {
        return ResponseEntity.ok(taskService.updateTaskStatus(taskId, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{taskId}/assign")
    public ResponseEntity<TaskResponse> updateAssignedUser(
            @PathVariable Long taskId,
            @RequestBody @Valid UpdateAssignedUserRequest request) {
        return ResponseEntity.ok(taskService.updateTasksAssignedUser(taskId, request));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> delete(@PathVariable Long taskId) {
        taskService.deleteTaskById(taskId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Page<TaskResponse>> searchTasks(
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) Long assignedUserId,
            @RequestParam(required = false) LocalDate dueBefore,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(
                taskService.searchTasks(projectId, status, assignedUserId, dueBefore, pageable)
        );
    }
}
