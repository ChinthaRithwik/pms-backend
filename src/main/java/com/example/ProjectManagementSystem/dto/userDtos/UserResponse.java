package com.example.ProjectManagementSystem.dto.userDtos;

import com.example.ProjectManagementSystem.entity.enums.Role;
import lombok.Data;

import java.time.Instant;

@Data
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private Role role;
    private Instant createdAt;

}
