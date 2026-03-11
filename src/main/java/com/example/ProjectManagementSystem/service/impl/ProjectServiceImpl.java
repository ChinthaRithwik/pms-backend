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
import com.example.ProjectManagementSystem.security.SecurityUtils;
import com.example.ProjectManagementSystem.service.ProjectService;
import com.example.ProjectManagementSystem.helper.AllowedTransitions;
import com.example.ProjectManagementSystem.entity.enums.TaskStatus;
import com.example.ProjectManagementSystem.entity.enums.Role;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ModelMapper modelMapper;
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
        logger.info("Fetching project id: {} for user: {}", id, owner.getName());
        // FIX C2: Admins can view any project, not just their own
        Project project;
        if (owner.getRole() == Role.ADMIN) {
            project = projectRepository.findById(id).orElseThrow(() -> {
                logger.warn("Project id: {} not found", id);
                return new ResourceNotFoundException("No Project found with ID: " + id);
            });
        } else {
            project = projectRepository.findByIdAndOwner(id, owner).orElseThrow(() -> {
                logger.warn("Project id: {} not found for owner: {}", id, owner.getName());
                return new ResourceNotFoundException("No Project found with ID: " + id);
            });
        }
        return modelMapper.map(project, ProjectResponse.class);
    }

    @Transactional
    @Override
    public ProjectResponse updateProjectById(UpdateProjectRequest request, Long id) {
        User owner = getAuthenticatedUser();
        logger.info("Updating project id: {} for owner: {}", id, owner.getName());

        Project project;
        if (owner.getRole() == Role.ADMIN) {
            // Admins can update any project
            project = projectRepository.findById(id).orElseThrow(() -> {
                logger.warn("Project id: {} not found", id);
                return new ResourceNotFoundException("No Project found with ID: " + id);
            });
        } else {
            project = projectRepository.findByIdAndOwner(id, owner).orElseThrow(() -> {
                logger.warn("Project id: {} not found for owner: {}", id, owner.getName());
                return new ResourceNotFoundException("No Project found with ID: " + id);
            });
        }

        project.setName(request.getName());
        project.setDescription(request.getDescription());
        logger.info("Project id: {} updated successfully", id);
        return modelMapper.map(project, ProjectResponse.class);
    }

    @Transactional
    @Override
    public ProjectResponse updateProjectStatus(Long id, UpdateProjectStatusRequest request) {
        User currentUser = getAuthenticatedUser();
        logger.info("User {} updating status of project id: {} to {}", currentUser.getName(), id, request.getStatus());

        // FIX M4: admins can change any project's status; owners can only change their own
        Project project;
        if (currentUser.getRole() == Role.ADMIN) {
            project = projectRepository.findById(id).orElseThrow(() -> {
                logger.warn("Project id: {} not found", id);
                return new ResourceNotFoundException("No Project found with ID: " + id);
            });
        } else {
            project = projectRepository.findByIdAndOwner(id, currentUser).orElseThrow(() -> {
                logger.warn("Project id: {} not found for owner: {}", id, currentUser.getName());
                return new ResourceNotFoundException("No Project found with ID: " + id);
            });
        }

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
        logger.info("Deleting project id: {} for user: {}", id, owner.getName());
        // FIX C2: Admins can delete any project, not just their own
        Project project;
        if (owner.getRole() == Role.ADMIN) {
            project = projectRepository.findById(id).orElseThrow(() -> {
                logger.warn("Project id: {} not found", id);
                return new ResourceNotFoundException("No Project found with ID: " + id);
            });
        } else {
            project = projectRepository.findByIdAndOwner(id, owner).orElseThrow(() -> {
                logger.warn("Project id: {} not found for owner: {}", id, owner.getName());
                return new ResourceNotFoundException("No Project found with ID: " + id);
            });
        }
        projectRepository.delete(project);
        logger.info("Project id: {} deleted", id);
    }

    @Override
    public Map<String, Long> getProjectStats() {
        User user = getAuthenticatedUser();
        Map<String, Long> stats = new LinkedHashMap<>();
        for (StatusTypes s : StatusTypes.values()) {
            stats.put(s.name(), projectRepository.countByOwnerAndStatus(user, s));
        }
        stats.put("TOTAL", projectRepository.countByOwner(user));
        return stats;
    }

    private User getAuthenticatedUser() {
        return securityUtils.getCurrentUser().orElseThrow(() ->
                new ResourceNotFoundException("Logged-in user not found"));
    }
}
