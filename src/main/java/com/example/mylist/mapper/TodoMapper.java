package com.example.mylist.mapper;

import com.example.mylist.dto.TodoRequest;
import com.example.mylist.dto.TodoResponse;
import com.example.mylist.model.Todo;
import org.springframework.stereotype.Component;

@Component
public class TodoMapper {

    public Todo toEntity(TodoRequest request, String ownerEmail) {
        Todo todo = new Todo();
        applyUpdates(todo, request);
        todo.setOwnerEmail(ownerEmail);
        return todo;
    }

    public void applyUpdates(Todo todo, TodoRequest request) {
        todo.setTitle(request.title().trim());
        todo.setTaskDescription(request.taskDescription() == null ? "" : request.taskDescription().trim());
        todo.setDueDate(request.dueDate());
        todo.setPriority(normalizePriority(request.priority()));
        todo.setCompleted(request.completed());
    }

    public TodoResponse toResponse(Todo todo) {
        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getTaskDescription(),
                todo.isCompleted(),
                todo.getDueDate(),
                todo.getPriority(),
                todo.getCreatedAt(),
                todo.getUpdatedAt()
        );
    }

    private String normalizePriority(String priority) {
        if (priority == null || priority.isBlank()) {
            return "medium";
        }
        return priority;
    }
}
