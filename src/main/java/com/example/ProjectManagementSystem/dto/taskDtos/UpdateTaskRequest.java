package com.example.ProjectManagementSystem.dto.taskDtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Data
public class UpdateTaskRequest {
    @NotBlank(message = "title should not be blank")
    private String title;
    // FIX C3: No @FutureOrPresent — allows editing tasks that already have a past due date
    private LocalDate dueDate;
}
