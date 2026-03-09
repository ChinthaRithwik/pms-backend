package com.example.ProjectManagementSystem.dto.taskDtos;

import com.example.ProjectManagementSystem.entity.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class UpdateTaskStatusRequest {
    @NotNull(message = "Status should not be Null")
    private TaskStatus status;
}
