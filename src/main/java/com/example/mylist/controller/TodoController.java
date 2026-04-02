package com.example.mylist.controller;

import com.example.mylist.model.Todo;
import com.example.mylist.service.TodoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/todos")
public class TodoController {

    private final TodoService service;

    public TodoController(TodoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Todo> getAllTasks() {
        return service.getAllTasks();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Todo addTask(@RequestBody Todo todo) {
        return service.addTask(todo);
    }

    @PutMapping("/{id}")
    public Todo updateTask(@PathVariable int id, @RequestBody Todo todo) {
        return service.updateTask(id, todo);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable int id) {
        service.deleteTask(id);
    }
}
