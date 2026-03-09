package com.example.ProjectManagementSystem.service;

import com.example.ProjectManagementSystem.dto.projectDtos.ProjectResponse;
import com.example.ProjectManagementSystem.dto.taskDtos.TaskResponse;
import com.example.ProjectManagementSystem.dto.userDtos.CreateUserRequest;
import com.example.ProjectManagementSystem.dto.userDtos.UserResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    UserResponse createUser(@Valid CreateUserRequest request);

    Page<UserResponse> getALlUsers(Pageable pageable);

    UserResponse getUserById(Long myId);

    void deleteUser(Long myId);

    Page<TaskResponse> getAllTasksByUser(Long myId, Pageable pageable);

    Page<ProjectResponse> getAllProjectsByUser(Long myId, Pageable pageable);
}
