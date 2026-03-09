package com.example.ProjectManagementSystem.dto.projectDtos;

import com.example.ProjectManagementSystem.entity.enums.StatusTypes;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class UpdateProjectRequest {
    @NotBlank(message = "Project name cannot be empty")
    @Size(max = 100)
    private String name;

    @Size(max = 500)
    private String description;

    private StatusTypes status;
}
