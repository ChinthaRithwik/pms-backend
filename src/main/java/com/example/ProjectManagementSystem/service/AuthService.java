package com.example.ProjectManagementSystem.service;

import com.example.ProjectManagementSystem.dto.userDtos.AuthDtos.LoginRequest;
import com.example.ProjectManagementSystem.dto.userDtos.AuthDtos.LoginResponse;
import com.example.ProjectManagementSystem.dto.userDtos.AuthDtos.SignupRequest;
import com.example.ProjectManagementSystem.dto.userDtos.AuthDtos.SignupResponse;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    SignupResponse signupUser(SignupRequest signupRequest);

    LoginResponse loginUser(LoginRequest loginRequest);
}
