package com.example.ProjectManagementSystem.service.impl;

import com.example.ProjectManagementSystem.dto.projectDtos.CreateProjectRequest;
import com.example.ProjectManagementSystem.dto.projectDtos.ProjectResponse;
import com.example.ProjectManagementSystem.dto.projectDtos.UpdateProjectRequest;
import com.example.ProjectManagementSystem.dto.projectDtos.UpdateProjectStatusRequest;
import com.example.ProjectManagementSystem.entity.Project;
import com.example.ProjectManagementSystem.entity.User;
import com.example.ProjectManagementSystem.entity.enums.StatusTypes;
import com.example.ProjectManagementSystem.exception.ResourceNotFoundException;
import com.example.ProjectManagementSystem.repository.ProjectRepository;
import com.example.ProjectManagementSystem.repository.UserRepository;
import com.example.ProjectManagementSystem.security.SecurityUtils;
import com.example.ProjectManagementSystem.service.ProjectService;
import com.example.ProjectManagementSystem.helper.AllowedTransitions;
import com.example.ProjectManagementSystem.entity.enums.TaskStatus;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(ProjectServiceImpl.class);
    private final SecurityUtils securityUtils;

    @Override
    public Page<ProjectResponse> getAllProjects(Pageable pageable) {
        User user = getAuthenticatedUser();
        logger.info("Fetching projects of user: {} page: {} size: {}",
                user.getName(), pageable.getPageNumber(), pageable.getPageSize());
        return projectRepository.findByOwner(user, pageable)
                .map(p -> modelMapper.map(p, ProjectResponse.class));
    }

    @Override
    public Page<ProjectResponse> getAllSystemProjects(Pageable pageable) {
        logger.info("Admin fetching all system projects page: {} size: {}",
                pageable.getPageNumber(), pageable.getPageSize());
        return projectRepository.findAll(pageable)
                .map(p -> modelMapper.map(p, ProjectResponse.class));
    }

    @Override
    public ProjectResponse createNewProject(CreateProjectRequest myProject) {

        User owner = getAuthenticatedUser();
        logger.info("Creating new project with name: {}", myProject.getName());
        Project project = new Project();
        project.setName(myProject.getName());
        project.setDescription(myProject.getDescription());
        project.setStatus(StatusTypes.PLANNED);
        project.setOwner(owner);
        Project saved = projectRepository.save(project);
        logger.info("Project created with id: {}", saved.getId());
        return modelMapper.map(saved, ProjectResponse.class);
    }

    @Override
    public ProjectResponse getProjectById(Long id) {
        User owner = getAuthenticatedUser();
        logger.info("Fetching project id: {} for owner: {}", id, owner.getName());
        Project project = projectRepository.findByIdAndOwner(id, owner).orElseThrow(() -> {
            logger.warn("Project id: {} not found for owner: {}", id, owner.getName());
            return new ResourceNotFoundException("No Project found with ID: " + id);
        });
        return modelMapper.map(project, ProjectResponse.class);
    }

    @Transactional
    @Override
    public ProjectResponse updateProjectById(UpdateProjectRequest request, Long id) {
        User owner = getAuthenticatedUser();
        logger.info("Updating project id: {} for owner: {}", id, owner.getName());
        Project project = projectRepository.findByIdAndOwner(id, owner).orElseThrow(() -> {
            logger.warn("Project id: {} not found for owner: {}", id, owner.getName());
            return new ResourceNotFoundException("No Project found with ID: " + id);
        });

        project.setName(request.getName());
        project.setDescription(request.getDescription());
        logger.info("Project id: {} updated successfully", id);
        return modelMapper.map(project, ProjectResponse.class);
    }

    @Transactional
    @Override
    public ProjectResponse updateProjectStatus(Long id, UpdateProjectStatusRequest request) {
        logger.info("Admin updating status of project id: {} to {}", id, request.getStatus());

        Project project = projectRepository.findById(id).orElseThrow(() -> {
            logger.warn("Project id: {} not found", id);
            return new ResourceNotFoundException("No Project found with ID: " + id);
        });

        StatusTypes currentStatus = project.getStatus();
        StatusTypes newStatus = request.getStatus();

        if (!AllowedTransitions.projectStatusAllowedTransitions.getOrDefault(currentStatus, java.util.Set.of()).contains(newStatus)) {
            logger.warn("Invalid project status transition from {} to {}", currentStatus, newStatus);
            throw new IllegalStateException("Cannot transition project status from " + currentStatus + " to " + newStatus);
        }

        if (newStatus == StatusTypes.COMPLETED) {
            long incompleteTasks = project.getTasks().stream()
                    .filter(t -> t.getStatus() != TaskStatus.COMPLETED)
                    .count();
            if (incompleteTasks > 0) {
                logger.warn("Cannot mark project id: {} as COMPLETED because it has {} incomplete tasks", id, incompleteTasks);
                throw new IllegalStateException("Cannot complete project because it has incomplete tasks.");
            }
        }

        project.setStatus(newStatus);
        logger.info("Project id: {} status updated to {}", id, newStatus);
        return modelMapper.map(project, ProjectResponse.class);
    }

    @Transactional
    @Override
    public void deleteProjectById(Long id) {
        User owner = getAuthenticatedUser();
        logger.info("Deleting project id: {} for owner: {}", id, owner.getName());
        Project project = projectRepository.findByIdAndOwner(id, owner).orElseThrow(() -> {
            logger.warn("Project id: {} not found for owner: {}", id, owner.getName());
            return new ResourceNotFoundException("No Project found with ID: " + id);
        });
        projectRepository.delete(project);
        logger.info("Project id: {} deleted", id);
    }

    public Optional<User> getCurrentUser() {
        String email = securityUtils.getCurrentUserEmail();
        return userRepository.findByEmail(email);
    }

    private User getAuthenticatedUser() {
        return getCurrentUser().orElseThrow(() ->
                new ResourceNotFoundException("Logged-in user not found"));
    }
}
