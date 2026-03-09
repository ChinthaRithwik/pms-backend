package com.example.ProjectManagementSystem.service.impl;

import com.example.ProjectManagementSystem.dto.userDtos.AuthDtos.LoginRequest;
import com.example.ProjectManagementSystem.dto.userDtos.AuthDtos.LoginResponse;
import com.example.ProjectManagementSystem.dto.userDtos.AuthDtos.SignupRequest;
import com.example.ProjectManagementSystem.dto.userDtos.AuthDtos.SignupResponse;
import com.example.ProjectManagementSystem.entity.User;
import com.example.ProjectManagementSystem.exception.InvalidCredentialsException;
import com.example.ProjectManagementSystem.exception.UserAlreadyExistsException;
import com.example.ProjectManagementSystem.repository.UserRepository;
import com.example.ProjectManagementSystem.security.JwtService;
import com.example.ProjectManagementSystem.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public SignupResponse signupUser(SignupRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email already in use");
        }
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))

                .build();
        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, SignupResponse.class);
    }

    @Override
    public LoginResponse loginUser(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());

        LoginResponse response = modelMapper.map(user, LoginResponse.class);
        response.setToken(token);
        return response;
    }
}
