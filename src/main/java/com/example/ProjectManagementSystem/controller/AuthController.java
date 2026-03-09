package com.example.ProjectManagementSystem.controller;

import com.example.ProjectManagementSystem.dto.userDtos.AuthDtos.LoginRequest;
import com.example.ProjectManagementSystem.dto.userDtos.AuthDtos.LoginResponse;
import com.example.ProjectManagementSystem.dto.userDtos.AuthDtos.SignupRequest;
import com.example.ProjectManagementSystem.dto.userDtos.AuthDtos.SignupResponse;
import com.example.ProjectManagementSystem.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication", description = "APIs for user signup and login")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account"
    )
    @ApiResponse(responseCode = "201", description = "User successfully created")
    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@RequestBody @Valid  SignupRequest signupRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.signupUser(signupRequest));
    }

    @Operation(
            summary = "Login user",
            description = "Authenticates the user and returns JWT token"
    )
    @ApiResponse(responseCode = "200", description = "User successfully authenticated")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest){
        return ResponseEntity.ok(authService.loginUser(loginRequest));
    }
}
