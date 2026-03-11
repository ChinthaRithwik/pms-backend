package com.example.ProjectManagementSystem.dto.projectDtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProjectRequest {
    @NotBlank(message = "Project name cannot be empty")
    @Size(max = 100)
    private String name;

    @Size(max = 500)
    private String description;
}
