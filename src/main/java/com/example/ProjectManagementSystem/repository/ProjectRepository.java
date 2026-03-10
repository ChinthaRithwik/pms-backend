package com.example.ProjectManagementSystem.repository;

import com.example.ProjectManagementSystem.dto.projectDtos.ProjectResponse;
import com.example.ProjectManagementSystem.entity.Project;
import com.example.ProjectManagementSystem.entity.User;
import com.example.ProjectManagementSystem.entity.enums.StatusTypes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project,Long> {

    boolean existsByOwnerId(Long myId);

    Page<Project> findByOwnerId(Long myid, Pageable pageable);

    Page<Project> findByOwner(User owner, Pageable pageable);

    Optional<Project> findByIdAndOwner(Long id, User owner);

    long countByOwner(User owner);

    long countByOwnerAndStatus(User owner, StatusTypes status);
}
