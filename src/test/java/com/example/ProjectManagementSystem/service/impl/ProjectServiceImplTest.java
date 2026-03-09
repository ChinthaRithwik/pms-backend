package com.example.ProjectManagementSystem.service.impl;

import com.example.ProjectManagementSystem.dto.projectDtos.CreateProjectRequest;
import com.example.ProjectManagementSystem.dto.projectDtos.ProjectResponse;
import com.example.ProjectManagementSystem.entity.Project;
import com.example.ProjectManagementSystem.entity.User;
import com.example.ProjectManagementSystem.entity.enums.StatusTypes;
import com.example.ProjectManagementSystem.repository.ProjectRepository;
import com.example.ProjectManagementSystem.repository.UserRepository;
import com.example.ProjectManagementSystem.security.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@test.com");
        mockUser.setName("Test User");
    }

    @Test
    void testCreateNewProject_Success() {
        // Arrange
        CreateProjectRequest request = new CreateProjectRequest();
        request.setName("Test Project");
        request.setDescription("Test Desc");

        Project savedProject = new Project();
        savedProject.setId(100L);
        savedProject.setName("Test Project");
        savedProject.setStatus(StatusTypes.PLANNED);

        ProjectResponse expectedResponse = new ProjectResponse();
        expectedResponse.setId(100L);
        expectedResponse.setName("Test Project");

        when(securityUtils.getCurrentUserEmail()).thenReturn("test@test.com");
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(mockUser));
        when(projectRepository.save(any(Project.class))).thenReturn(savedProject);
        when(modelMapper.map(any(Project.class), eq(ProjectResponse.class))).thenReturn(expectedResponse);

        // Act
        ProjectResponse actualResponse = projectService.createNewProject(request);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(100L, actualResponse.getId());
        assertEquals("Test Project", actualResponse.getName());
        verify(projectRepository, times(1)).save(any(Project.class));
    }
}
