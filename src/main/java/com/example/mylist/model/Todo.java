package com.example.mylist.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Title cannot be empty")
    private String title;

    private String taskDescription;

    private boolean completed;

    public Todo() {}

    public Todo(Integer id, String title, String taskDescription, boolean completed) {
        this.id = id;
        this.title = title;
        this.taskDescription = taskDescription;
        this.completed = completed;
    }

    public Integer getId() { return id; }
    public String getTitle() { return title; }
    public String getTaskDescription() { return taskDescription; }
    public boolean isCompleted() { return completed; }

    public void setId(Integer id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setTaskDescription(String taskDescription) { this.taskDescription = taskDescription; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}
