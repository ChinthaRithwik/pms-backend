package com.example.ProjectManagementSystem.service;

import com.example.ProjectManagementSystem.dto.projectDtos.ProjectResponse;
import com.example.ProjectManagementSystem.dto.projectDtos.UpdateProjectRequest;
import com.example.ProjectManagementSystem.dto.projectDtos.UpdateProjectStatusRequest;
import com.example.ProjectManagementSystem.dto.projectDtos.CreateProjectRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectService {

    Page<ProjectResponse> getAllProjects(Pageable pageable);

    Page<ProjectResponse> getAllSystemProjects(Pageable pageable);

    ProjectResponse createNewProject(CreateProjectRequest myProject);

    ProjectResponse getProjectById(Long id);

    ProjectResponse updateProjectById(UpdateProjectRequest request, Long id);

    ProjectResponse updateProjectStatus(Long id, UpdateProjectStatusRequest request);

    void deleteProjectById(Long id);
}
