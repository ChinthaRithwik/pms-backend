package com.example.ProjectManagementSystem.repository;

import com.example.ProjectManagementSystem.entity.Project;
import com.example.ProjectManagementSystem.entity.Task;
import com.example.ProjectManagementSystem.entity.enums.TaskStatus;
import jakarta.persistence.metamodel.SingularAttribute;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task,Long>, JpaSpecificationExecutor<Task> {

    Page<Task> findByProjectId(Long projectId, Pageable pageable);

    Page<Task> findByProjectIdAndStatus(Long projectId,TaskStatus status,Pageable pageable);

    long countByProjectIdAndStatusNot(Long projectId, TaskStatus status);

    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.tasks WHERE p.id = :id")
    Optional<Project> findByIdWithTasks(Long id);

    long countByProjectId(Long id);

    boolean existsByAssignedUserId(Long myId);

    Page<Task> findByAssignedUserId(Long myId,Pageable pageable);

    Page<Task> findByDueDateBeforeAndStatusNot(LocalDate dueDate,TaskStatus status,Pageable pageable);
}
