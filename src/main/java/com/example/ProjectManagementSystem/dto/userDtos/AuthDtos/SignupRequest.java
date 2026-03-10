package com.example.ProjectManagementSystem.dto.userDtos.AuthDtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;

@Data
public class SignupRequest {

    @Schema(example = "rithwik")
    @NotBlank(message = "Name should not be blank")
    @Size(max = 50)
    private String name;

    @Schema(example = "rithwik@gmail.com")
    @NotBlank(message = "Email should not be blank")
    @Email(message = "Email is invalid")
    private String email;

    @Schema(example = "password123")
    @NotBlank(message = "Password should not be blank")
    @Size(min=8)
    private String password;
}
