package com.example.mylist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MylistApplication {

    public static void main(String[] args) {
        SpringApplication.run(MylistApplication.class, args);
    }

}


class ToDoList {
    private int id;
    private String title;
    private String task_description;

    public ToDoList(int id, String title, String task_description) {
        this.id = id;
        this.title = title;
        this.task_description = task_description;
    }

    public int getId() { return this.id; }
    public String getTitle() { return this.title; }
    public String getTask_description() { return this.task_description; }

    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setTask_description(String task_description) { this.task_description = task_description; }
}