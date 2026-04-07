package com.example.mylist.application.mapper;

import com.example.mylist.application.dto.TodoRequest;
import com.example.mylist.application.dto.TodoResponse;
import com.example.mylist.domain.model.Priority;
import com.example.mylist.domain.model.Todo;
import org.springframework.stereotype.Component;

@Component
public class TodoMapper {

    public Todo toEntity(TodoRequest request, String ownerEmail) {
        return Todo.create(
                request.title().trim(),
                normalizeDescription(request.taskDescription()),
                request.completed(),
                request.dueDate(),
                Priority.from(request.priority()),
                ownerEmail
        );
    }

    public void applyUpdates(Todo todo, TodoRequest request) {
        todo.updateDetails(
                request.title().trim(),
                normalizeDescription(request.taskDescription()),
                request.completed(),
                request.dueDate(),
                Priority.from(request.priority())
        );
    }

    public TodoResponse toResponse(Todo todo) {
        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getTaskDescription(),
                todo.isCompleted(),
                todo.getDueDate(),
                todo.getPriority().toApiValue(),
                todo.getCreatedAt(),
                todo.getUpdatedAt()
        );
    }

    private String normalizeDescription(String taskDescription) {
        return taskDescription == null ? "" : taskDescription.trim();
    }
}
