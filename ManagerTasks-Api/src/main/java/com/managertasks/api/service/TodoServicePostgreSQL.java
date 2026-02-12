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

@Service("todoServicePostgreSQL")
public class TodoServicePostgreSQL implements TodoService {

    @Autowired
    private TodoRepository todoRepository;

    @Override
    @Transactional
    public Object createTodo(TodoRecord todoRecord) {
        // Convert TodoRecord to Todo entity
        Todo todo = new Todo(
            todoRecord.name(),
            PriorityEnum.valueOf(todoRecord.priority()),
            UUID.fromString(todoRecord.userId())
        );

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
            "updatedAt", todo.getUpdatedAt()
        );
    }

}
