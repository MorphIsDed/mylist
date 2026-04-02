package com.example.mylist.model;

import jakarta.persistence.*;

@Entity
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;
    private String taskDescription;

    public Todo() {}

    public Todo(Integer id, String title, String taskDescription) {
        this.id = id;
        this.title = title;
        this.taskDescription = taskDescription;
    }

    public Integer getId() { return id; }
    public String getTitle() { return title; }
    public String getTaskDescription() { return taskDescription; }

    public void setId(Integer id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setTaskDescription(String taskDescription) { this.taskDescription = taskDescription; }
}
