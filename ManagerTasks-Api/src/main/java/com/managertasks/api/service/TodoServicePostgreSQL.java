package com.managertasks.api.service;

import com.managertasks.api.dto.request.TodoRecord;
import com.managertasks.api.entity.PriorityEnum;
import com.managertasks.api.entity.Todo;
import com.managertasks.api.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

// @Service Lifecycle:
// 1. CLASS DETECTION: Spring scans classpath and finds @Service annotation
// 2. BEAN REGISTRATION: Class is registered as a Spring bean (service layer component)
// 3. BEAN NAME: Service name "todoServicePostgreSQL" is stored for identification
// 4. INSTANTIATION: TodoServicePostgreSQL instance is created by Spring
// 5. DEPENDENCY INJECTION: @Autowired TodoRepository is injected into the service
// 6. INITIALIZATION: Bean is fully initialized and added to ApplicationContext
// 7. READY: Service bean is ready to handle business logic and can be injected into controllers

@Service("todoServicePostgreSQL")
public class TodoServicePostgreSQL implements TodoService {

    @Autowired
    private TodoRepository todoRepository;

    // @Transactional Explanation:
    // Manages database transactions for methods (automatic commit/rollback)
    // Lifecycle: 1) Method call detected
    // 2) Spring starts a new database transaction
    // 3) All database operations are executed within transaction
    // 4) If method completes normally → transaction automatically commits
    // 5) If exception is thrown → transaction automatically rolls back
    // 6) All changes are either completely saved or completely reverted
    // Attributes: readOnly=true (prevents writes), propagation (transaction scope),
    // isolation level
    // Use cases: @Transactional on write operations (create, update, delete)
    // @Transactional(readOnly=true) on read operations for optimization
    // Without @Transactional: Each database operation auto-commits immediately
    // (risky for multi-step operations)

    @Override
    @Transactional
    public Object createTodo(TodoRecord todoRecord) {
        // Convert TodoRecord to Todo entity
        Todo todo = new Todo(
                todoRecord.name(),
                PriorityEnum.valueOf(todoRecord.priority()),
                UUID.fromString(todoRecord.userId()));

        // Set completed status if provided
        if (todoRecord.completed() != null) {
            todo.setCompleted(todoRecord.completed());
        }

        // Save to database
        Todo savedTodo = todoRepository.save(todo);

        // Convert back to map for response
        return convertTodoToMap(savedTodo);
    }

    @Override
    @Transactional(readOnly = true)
    public Object getTodoById(Long id) {
        // Note: Interface uses Long, but database uses UUID
        // This is a simplified implementation - consider refactoring interface
        throw new UnsupportedOperationException("Use getTodoByUUID instead");
    }

    @Transactional(readOnly = true)
    public Object getTodoByUUID(UUID id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Todo not found with ID: " + id));
        return convertTodoToMap(todo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object> getAllTodos() {
        return todoRepository.findAll()
                .stream()
                .map(this::convertTodoToMap)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Object> getTodosByUserId(UUID userId) {
        return todoRepository.findByUserId(userId)
                .stream()
                .map(this::convertTodoToMap)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Object> getTodosByUserIdAndCompleted(UUID userId, Boolean completed) {
        return todoRepository.findByUserIdAndCompleted(userId, completed)
                .stream()
                .map(this::convertTodoToMap)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Object> getTodosByUserIdAndPriority(UUID userId, String priority) {
        return todoRepository.findByUserIdAndPriority(userId, PriorityEnum.valueOf(priority))
                .stream()
                .map(this::convertTodoToMap)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Object updateTodo(Long id, TodoRecord todoRecord) {
        // Note: Interface uses Long, but database uses UUID
        // This is a simplified implementation - consider refactoring interface
        throw new UnsupportedOperationException("Use updateTodoByUUID instead");
    }

    @Transactional
    public Object updateTodoByUUID(UUID id, TodoRecord todoRecord) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Todo not found with ID: " + id));

        // Update fields from TodoRecord
        todo.setName(todoRecord.name());
        todo.setPriority(PriorityEnum.valueOf(todoRecord.priority()));
        if (todoRecord.completed() != null) {
            todo.setCompleted(todoRecord.completed());
        }
        todo.setUserId(UUID.fromString(todoRecord.userId()));

        // Save updated todo
        Todo updatedTodo = todoRepository.save(todo);

        return convertTodoToMap(updatedTodo);
    }

    @Override
    @Transactional
    public void deleteTodo(Long id) {
        // Note: Interface uses Long, but database uses UUID
        // This is a simplified implementation - consider refactoring interface
        throw new UnsupportedOperationException("Use deleteTodoByUUID instead");
    }

    @Transactional
    public void deleteTodoByUUID(UUID id) {
        if (!todoRepository.existsById(id)) {
            throw new IllegalArgumentException("Todo not found with ID: " + id);
        }
        todoRepository.deleteById(id);
    }

    // Helper method to convert Todo entity to Map (for response)
    private Object convertTodoToMap(Todo todo) {
        return java.util.Map.of(
                "id", todo.getId(),
                "name", todo.getName(),
                "priority", todo.getPriority().name(),
                "completed", todo.getCompleted(),
                "userId", todo.getUserId(),
                "createdAt", todo.getCreatedAt(),
                "updatedAt", todo.getUpdatedAt());
    }

}
