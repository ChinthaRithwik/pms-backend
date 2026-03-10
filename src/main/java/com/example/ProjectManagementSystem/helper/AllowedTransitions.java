package com.example.ProjectManagementSystem.helper;

import com.example.ProjectManagementSystem.entity.enums.StatusTypes;
import com.example.ProjectManagementSystem.entity.enums.TaskStatus;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

public class AllowedTransitions {
    public static final Map<TaskStatus, Set<TaskStatus>> allowedTransitions = new EnumMap<>(TaskStatus.class);
    static {
        allowedTransitions.put(TaskStatus.TODO,
                Set.of(TaskStatus.TODO, TaskStatus.IN_PROGRESS));

        allowedTransitions.put(TaskStatus.IN_PROGRESS,
                Set.of(TaskStatus.IN_PROGRESS, TaskStatus.COMPLETED, TaskStatus.BLOCKED));

        allowedTransitions.put(TaskStatus.BLOCKED,
                Set.of(TaskStatus.BLOCKED, TaskStatus.IN_PROGRESS));

        allowedTransitions.put(TaskStatus.COMPLETED,
                Set.of(TaskStatus.COMPLETED));
    }

    public static final Map<StatusTypes, Set<StatusTypes>> projectStatusAllowedTransitions = new EnumMap<>(StatusTypes.class);
    static {
        projectStatusAllowedTransitions.put(StatusTypes.PLANNED,
                Set.of(StatusTypes.PLANNED, StatusTypes.IN_PROGRESS, StatusTypes.CANCELLED));

        projectStatusAllowedTransitions.put(StatusTypes.IN_PROGRESS,
                Set.of(StatusTypes.IN_PROGRESS, StatusTypes.ON_HOLD, StatusTypes.COMPLETED, StatusTypes.CANCELLED));

        projectStatusAllowedTransitions.put(StatusTypes.ON_HOLD,
                Set.of(StatusTypes.ON_HOLD, StatusTypes.IN_PROGRESS, StatusTypes.CANCELLED));

        projectStatusAllowedTransitions.put(StatusTypes.COMPLETED,
                Set.of(StatusTypes.COMPLETED));

        projectStatusAllowedTransitions.put(StatusTypes.CANCELLED,
                Set.of(StatusTypes.CANCELLED));
    }
}
