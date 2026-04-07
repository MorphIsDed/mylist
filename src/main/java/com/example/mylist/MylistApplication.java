package com.example.mylist;

import com.example.mylist.infrastructure.config.DotenvLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MylistApplication {
    public static void main(String[] args) {
        DotenvLoader.loadIntoSystemProperties();
        SpringApplication.run(MylistApplication.class, args);
    }
}