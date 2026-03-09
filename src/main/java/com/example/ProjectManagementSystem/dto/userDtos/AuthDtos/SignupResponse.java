package com.example.ProjectManagementSystem.dto.userDtos.AuthDtos;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupResponse {
    private Long id;
    private String name;
    private String email;
}
