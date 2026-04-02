package com.example.mylist.service;

import com.example.mylist.model.Todo;
import com.example.mylist.repository.TodoRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

@Service
public class TodoService {

    private final TodoRepository repo;

    public TodoService(TodoRepository repo) {
        this.repo = repo;
    }

    public List<Todo> getAllTasks() {
        return repo.findAll();
    }

    public Todo addTask(Todo todo) {
        return repo.save(todo);
    }

    public void deleteTask(int id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo not found");
        }
        repo.deleteById(id);
    }

    public Todo updateTask(int id, Todo newTodo) {
        Todo todo = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo not found"));
        todo.setTitle(newTodo.getTitle());
        todo.setTaskDescription(newTodo.getTaskDescription());
        return repo.save(todo);
    }
}
