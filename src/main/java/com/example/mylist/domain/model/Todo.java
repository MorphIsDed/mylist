package com.example.mylist.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(indexes = {
        @Index(name = "idx_todo_owner_completed", columnList = "ownerEmail, completed"),
        @Index(name = "idx_todo_owner_due_date", columnList = "ownerEmail, dueDate")
})
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Title cannot be empty")
    private String title;

    private String taskDescription;

    private boolean completed;

    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Priority priority = Priority.MEDIUM;

    @Column(nullable = false, length = 320)
    private String ownerEmail;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    protected Todo() {}

    private Todo(String title, String taskDescription, boolean completed, LocalDate dueDate, Priority priority, String ownerEmail) {
        this.title = title;
        this.taskDescription = taskDescription;
        this.completed = completed;
        this.dueDate = dueDate;
        this.priority = priority;
        this.ownerEmail = ownerEmail;
    }

    public static Todo create(String title, String taskDescription, boolean completed, LocalDate dueDate, Priority priority, String ownerEmail) {
        return new Todo(title, taskDescription, completed, dueDate, priority, ownerEmail);
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    public Integer getId() { return id; }
    public String getTitle() { return title; }
    public String getTaskDescription() { return taskDescription; }
    public boolean isCompleted() { return completed; }
    public LocalDate getDueDate() { return dueDate; }
    public Priority getPriority() { return priority; }
    public String getOwnerEmail() { return ownerEmail; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void updateDetails(String title, String taskDescription, boolean completed, LocalDate dueDate, Priority priority) {
        this.title = title;
        this.taskDescription = taskDescription;
        this.completed = completed;
        this.dueDate = dueDate;
        this.priority = priority;
    }

    public void toggleCompleted() {
        completed = !completed;
    }
}
