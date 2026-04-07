package com.example.mylist.interfaces.rest.controller;

import com.example.mylist.application.dto.TodoRequest;
import com.example.mylist.application.dto.TodoResponse;
import com.example.mylist.infrastructure.security.AuthenticatedUserService;
import com.example.mylist.application.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoService service;
    private final AuthenticatedUserService authenticatedUserService;

    public TodoController(TodoService service, AuthenticatedUserService authenticatedUserService) {
        this.service = service;
        this.authenticatedUserService = authenticatedUserService;
    }

    @GetMapping
    public List<TodoResponse> getAllTasks(Authentication authentication) {
        return service.getAllTasks(authenticatedUserService.email(authentication));
    }

    @GetMapping("/export")
    public ResponseEntity<List<TodoResponse>> exportTasks(Authentication authentication) {
        String ownerEmail = authenticatedUserService.email(authentication);
        String filename = "mylist-backup-" + LocalDate.now() + ".json";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.getAllTasks(ownerEmail));
    }

    @GetMapping("/{id}")
    public TodoResponse getTask(@PathVariable int id, Authentication authentication) {
        return service.getTaskById(id, authenticatedUserService.email(authentication));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TodoResponse addTask(@Valid @RequestBody TodoRequest todo, Authentication authentication) {
        return service.addTask(todo, authenticatedUserService.email(authentication));
    }

    @PutMapping("/{id}")
    public TodoResponse updateTask(@PathVariable int id, @Valid @RequestBody TodoRequest todo, Authentication authentication) {
        return service.updateTask(id, todo, authenticatedUserService.email(authentication));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable int id, Authentication authentication) {
        service.deleteTask(id, authenticatedUserService.email(authentication));
    }

    @PutMapping("/toggle/{id}")
    public TodoResponse toggleTask(@PathVariable int id, Authentication authentication) {
        return service.toggleStatus(id, authenticatedUserService.email(authentication));
    }
}
