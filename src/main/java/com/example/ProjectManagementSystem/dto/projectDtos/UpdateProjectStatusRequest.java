package com.example.ProjectManagementSystem.dto.projectDtos;

import com.example.ProjectManagementSystem.entity.enums.StatusTypes;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateProjectStatusRequest {

    @NotNull(message = "Status must not be null")
    private StatusTypes status;
}
