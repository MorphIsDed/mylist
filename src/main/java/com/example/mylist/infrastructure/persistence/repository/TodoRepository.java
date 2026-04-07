package com.example.mylist.infrastructure.persistence.repository;

import com.example.mylist.domain.model.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Integer> {
    List<Todo> findAllByOwnerEmailOrderByCompletedAscDueDateAscCreatedAtDesc(String ownerEmail);

    Optional<Todo> findByIdAndOwnerEmail(Integer id, String ownerEmail);
}
