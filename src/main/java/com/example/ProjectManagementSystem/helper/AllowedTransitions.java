package com.example.ProjectManagementSystem.helper;

import com.example.ProjectManagementSystem.entity.enums.TaskStatus;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

public class AllowedTransitions {
    public static final Map<TaskStatus, Set<TaskStatus>> allowedTransitions = new EnumMap<>(TaskStatus.class);
    static {
        allowedTransitions.put(TaskStatus.TODO,
                Set.of(TaskStatus.IN_PROGRESS));

        allowedTransitions.put(TaskStatus.IN_PROGRESS,
                Set.of(TaskStatus.COMPLETED, TaskStatus.BLOCKED));

        allowedTransitions.put(TaskStatus.BLOCKED,
                Set.of(TaskStatus.IN_PROGRESS));

        allowedTransitions.put(TaskStatus.COMPLETED,
                Set.of());
    }
}
