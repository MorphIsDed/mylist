package com.example.mylist.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record TodoRequest(
        @NotBlank(message = "Title cannot be empty")
        @Size(max = 120, message = "Title must be at most 120 characters")
        String title,

        @Size(max = 400, message = "Description must be at most 400 characters")
        String taskDescription,

        LocalDate dueDate,

        @Pattern(regexp = "low|medium|high", message = "Priority must be low, medium, or high")
        String priority,

        boolean completed
) {
}
