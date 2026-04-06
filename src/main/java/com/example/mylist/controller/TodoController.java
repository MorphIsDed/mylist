package com.example.mylist.controller;

import com.example.mylist.model.Todo;
import com.example.mylist.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/todos")
@CrossOrigin("*")
public class TodoController {

    private final TodoService service;

    public TodoController(TodoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Todo> getAllTasks() {
        return service.getAllTasks();
    }

    @GetMapping("/{id}")
    public Todo getTask(@PathVariable int id) {
        return service.getTaskById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Todo addTask(@Valid @RequestBody Todo todo) {
        return service.addTask(todo);
    }

    @PutMapping("/{id}")
    public Todo updateTask(@PathVariable int id, @RequestBody Todo todo) {
        return service.updateTask(id, todo);
    }

    @DeleteMapping("/{id}")
    public String deleteTask(@PathVariable int id) {
        service.deleteTask(id);
        return "Task deleted successfully";
    }

    @PutMapping("/toggle/{id}")
    public Todo toggleTask(@PathVariable int id) {
        return service.toggleStatus(id);
    }
}
