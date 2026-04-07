package com.example.mylist.application.service;

import com.example.mylist.application.dto.TodoRequest;
import com.example.mylist.application.dto.TodoResponse;
import com.example.mylist.domain.exception.ResourceNotFoundException;
import com.example.mylist.application.mapper.TodoMapper;
import com.example.mylist.domain.model.Todo;
import com.example.mylist.infrastructure.persistence.repository.TodoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TodoService {

    private final TodoRepository repo;
    private final TodoMapper mapper;

    public TodoService(TodoRepository repo, TodoMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<TodoResponse> getAllTasks(String ownerEmail) {
        return repo.findAllByOwnerEmailOrderByCompletedAscDueDateAscCreatedAtDesc(ownerEmail)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TodoResponse getTaskById(int id, String ownerEmail) {
        return mapper.toResponse(getOwnedTask(id, ownerEmail));
    }

    public TodoResponse addTask(TodoRequest request, String ownerEmail) {
        Todo todo = mapper.toEntity(request, ownerEmail);
        return mapper.toResponse(repo.save(todo));
    }

    public TodoResponse updateTask(int id, TodoRequest request, String ownerEmail) {
        Todo todo = getOwnedTask(id, ownerEmail);
        mapper.applyUpdates(todo, request);
        return mapper.toResponse(repo.save(todo));
    }

    public void deleteTask(int id, String ownerEmail) {
        repo.delete(getOwnedTask(id, ownerEmail));
    }

    public TodoResponse toggleStatus(int id, String ownerEmail) {
        Todo todo = getOwnedTask(id, ownerEmail);
        todo.toggleCompleted();
        return mapper.toResponse(repo.save(todo));
    }

    private Todo getOwnedTask(int id, String ownerEmail) {
        return repo.findByIdAndOwnerEmail(id, ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id " + id));
    }
}
