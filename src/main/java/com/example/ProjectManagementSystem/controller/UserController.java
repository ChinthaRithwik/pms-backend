package com.example.ProjectManagementSystem.controller;

import com.example.ProjectManagementSystem.dto.projectDtos.ProjectResponse;
import com.example.ProjectManagementSystem.dto.taskDtos.TaskResponse;
import com.example.ProjectManagementSystem.dto.userDtos.CreateUserRequest;
import com.example.ProjectManagementSystem.dto.userDtos.UserResponse;
import com.example.ProjectManagementSystem.service.UserService;
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

@SecurityRequirement(name = "BearerAuth")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<UserResponse> create(@RequestBody @Valid CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAll(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(userService.getALlUsers(pageable));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{myId}")
    public ResponseEntity<UserResponse> getById(@PathVariable Long myId) {
        return ResponseEntity.ok(userService.getUserById(myId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{myId}")
    public ResponseEntity<Void> delete(@PathVariable Long myId) {
        userService.deleteUser(myId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{myId}/tasks")
    public ResponseEntity<Page<TaskResponse>> getTasks(
            @PathVariable Long myId,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(userService.getAllTasksByUser(myId, pageable));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{myId}/projects")
    public ResponseEntity<Page<ProjectResponse>> getProjects(
            @PathVariable Long myId,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(userService.getAllProjectsByUser(myId, pageable));
    }
}
