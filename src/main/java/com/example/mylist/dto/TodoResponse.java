package com.example.mylist.dto;

import java.time.Instant;
import java.time.LocalDate;

public record TodoResponse(
        Integer id,
        String title,
        String taskDescription,
        boolean completed,
        LocalDate dueDate,
        String priority,
        Instant createdAt,
        Instant updatedAt
) {
}
