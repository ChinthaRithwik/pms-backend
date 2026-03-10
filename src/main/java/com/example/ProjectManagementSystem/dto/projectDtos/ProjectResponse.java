package com.example.ProjectManagementSystem.dto.projectDtos;

import com.example.ProjectManagementSystem.entity.enums.StatusTypes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectResponse {
    private Long id;
    private String name;
    private String description;
    private StatusTypes status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
