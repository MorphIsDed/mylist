package com.example.mylist.repository;

import com.example.mylist.model.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Integer> {
    List<Todo> findAllByOwnerEmailOrderByCompletedAscDueDateAscCreatedAtDesc(String ownerEmail);

    Optional<Todo> findByIdAndOwnerEmail(Integer id, String ownerEmail);
}
