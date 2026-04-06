package com.example.mylist.service;

import com.example.mylist.exception.ResourceNotFoundException;
import com.example.mylist.model.Todo;
import com.example.mylist.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoService {

    @Autowired
    private TodoRepository repo;

    public List<Todo> getAllTasks() {
        return repo.findAll();
    }

    public Todo getTaskById(int id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id " + id));
    }

    public Todo addTask(Todo todo) {
        return repo.save(todo);
    }

    public Todo updateTask(int id, Todo newTodo) {
        Todo todo = getTaskById(id);

        todo.setTitle(newTodo.getTitle());
        todo.setTaskDescription(newTodo.getTaskDescription());
        todo.setCompleted(newTodo.isCompleted());

        return repo.save(todo);
    }

    public void deleteTask(int id) {
        Todo todo = getTaskById(id);
        repo.delete(todo);
    }

    public Todo toggleStatus(int id) {
        Todo todo = getTaskById(id);
        todo.setCompleted(!todo.isCompleted());
        return repo.save(todo);
    }
}