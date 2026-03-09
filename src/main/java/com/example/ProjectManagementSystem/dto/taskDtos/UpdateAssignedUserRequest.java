package com.example.ProjectManagementSystem.dto.taskDtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class UpdateAssignedUserRequest {
    private Long assignedUserId;
}
