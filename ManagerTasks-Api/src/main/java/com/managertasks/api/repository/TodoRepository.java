package com.managertasks.api.repository;

import com.managertasks.api.entity.PriorityEnum;
import com.managertasks.api.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

// @Repository Lifecycle:
// 1. CLASS DETECTION: Spring scans classpath and finds @Repository annotation on interface
// 2. PROXY GENERATION: Spring Data JPA generates a proxy implementation of the interface
// 3. BEAN REGISTRATION: Generated proxy is registered as a Spring bean (data access component)
// 4. DATABASE CONNECTION: Proxy configures database connection using application.properties
// 5. METHOD IMPLEMENTATION: Query methods (findBy*, existsBy*) are implemented automatically
// 6. INITIALIZATION: Repository bean is fully initialized and added to ApplicationContext
// 7. READY: Repository can be injected into services for database operations

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
