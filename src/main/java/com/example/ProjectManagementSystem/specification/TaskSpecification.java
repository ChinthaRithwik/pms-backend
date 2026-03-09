package com.example.ProjectManagementSystem.specification;

import com.example.ProjectManagementSystem.entity.Task;
import com.example.ProjectManagementSystem.entity.enums.TaskStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class TaskSpecification {

    public static Specification<Task> hasStatus(TaskStatus status) {
        return (root, query, cb) -> {
            if (status == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("status"), status);
        };
    }

    public static Specification<Task> hasProject(Long projectId) {
        return (root, query, cb) -> {
            if (projectId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("project").get("id"), projectId);
        };
    }

    public static Specification<Task> hasAssignedUser(Long userId) {
        return (root, query, cb) -> {
            if (userId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("assignedUser").get("id"), userId);
        };
    }

    public static Specification<Task> dueBefore(LocalDate date) {
        return (root, query, cb) -> {
            if (date == null) {
                return cb.conjunction();
            }
            return cb.lessThanOrEqualTo(root.get("dueDate"), date);
        };
    }
}