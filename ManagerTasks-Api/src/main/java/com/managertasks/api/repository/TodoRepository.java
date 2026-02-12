package com.managertasks.api.repository;

import com.managertasks.api.entity.PriorityEnum;
import com.managertasks.api.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TodoRepository extends JpaRepository<Todo, UUID> {

    // Find all todos for a specific user
    List<Todo> findByUserId(UUID userId);

    // Find completed todos for a specific user
    List<Todo> findByUserIdAndCompleted(UUID userId, Boolean completed);

    // Find todos by priority for a specific user
    List<Todo> findByUserIdAndPriority(UUID userId, PriorityEnum priority);

    // Check if a todo exists for a specific user
    boolean existsByIdAndUserId(UUID id, UUID userId);

}
