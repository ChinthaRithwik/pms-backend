package com.example.ProjectManagementSystem.service.impl;

import com.example.ProjectManagementSystem.dto.projectDtos.ProjectResponse;
import com.example.ProjectManagementSystem.dto.taskDtos.TaskResponse;
import com.example.ProjectManagementSystem.dto.userDtos.CreateUserRequest;
import com.example.ProjectManagementSystem.dto.userDtos.UserResponse;
import com.example.ProjectManagementSystem.entity.User;
import com.example.ProjectManagementSystem.exception.ResourceNotFoundException;
import com.example.ProjectManagementSystem.repository.ProjectRepository;
import com.example.ProjectManagementSystem.repository.TaskRepository;
import com.example.ProjectManagementSystem.repository.UserRepository;
import com.example.ProjectManagementSystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    private final PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public UserResponse createUser(CreateUserRequest request) {
        logger.info("Creating user with email: {}", request.getEmail());
        if (userRepository.existsByEmail(request.getEmail())) {
            logger.warn("User creation failed. Email already exists: {}", request.getEmail());
            throw new IllegalStateException("Email already exists");
        }
        User user = new User();
        modelMapper.map(request, user);

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);
        logger.info("User created successfully with id: {}", savedUser.getId());
        UserResponse response = modelMapper.map(savedUser, UserResponse.class);
        response.setRole(savedUser.getRole());
        return response;
    }

    @Override
    public Page<UserResponse> getALlUsers(Pageable pageable) {
        logger.info("Fetching users with page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return userRepository.findAll(pageable).map(u -> {
            UserResponse response = modelMapper.map(u, UserResponse.class);
            response.setRole(u.getRole());
            return response;
        });
    }

    @Override
    public UserResponse getUserById(Long myId) {
        logger.info("Fetching user with id: {}", myId);
        User user = userRepository.findById(myId).orElseThrow(() -> {
            logger.warn("User not found with id: {}", myId);
            return new ResourceNotFoundException("User not found with ID: " + myId);
        });
        UserResponse response = modelMapper.map(user, UserResponse.class);
        response.setRole(user.getRole());
        return response;
    }

    @Override
    public void deleteUser(Long myId) {
        logger.info("Attempting to delete user with id: {}", myId);
        User user = userRepository.findById(myId).orElseThrow(() -> {
            logger.warn("User not found with id: {}", myId);
            return new ResourceNotFoundException("User not found with ID: " + myId);
        });
        if (projectRepository.existsByOwnerId(myId))
            throw new IllegalStateException("User cannot be deleted. Reassign Projects first");
        if (taskRepository.existsByAssignedUserId(myId))
            throw new IllegalStateException("User cannot be deleted. Reassign tasks first");
        userRepository.delete(user);
        logger.info("User deleted successfully with id: {}", myId);
    }

    @Override
    public Page<TaskResponse> getAllTasksByUser(Long myId, Pageable pageable) {
        if (!userRepository.existsById(myId))
            throw new ResourceNotFoundException("User not found with ID: " + myId);
        return taskRepository.findByAssignedUserId(myId, pageable).map(task -> {
            TaskResponse response = modelMapper.map(task, TaskResponse.class);
            response.setProjectId(task.getProject().getId());
            response.setAssignedUserId(task.getAssignedUser() != null ? task.getAssignedUser().getId() : null);
            return response;
        });
    }

    @Override
    public Page<ProjectResponse> getAllProjectsByUser(Long myId, Pageable pageable) {
        if (!userRepository.existsById(myId))
            throw new ResourceNotFoundException("User not found with ID: " + myId);
        return projectRepository.findByOwnerId(myId, pageable)
                .map(p -> modelMapper.map(p, ProjectResponse.class));
    }
}