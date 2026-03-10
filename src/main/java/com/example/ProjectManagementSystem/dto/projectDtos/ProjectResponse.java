package com.example.ProjectManagementSystem.dto.projectDtos;

import com.example.ProjectManagementSystem.entity.enums.StatusTypes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectResponse {
    private Long id;
    private String name;
    private String description;
    private StatusTypes status;
    private Instant createdAt;
    private Instant updatedAt;

}
