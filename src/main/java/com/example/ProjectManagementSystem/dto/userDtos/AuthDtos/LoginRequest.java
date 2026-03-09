package com.example.ProjectManagementSystem.dto.userDtos.AuthDtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Email should not be blank")
    @Email(message = "Email is invalid")
    private String email;

    @NotBlank(message = "Password should not be blank")
    @Size(min=8)
    private String password;
}
