package com.managertasks.api.service;

import com.managertasks.api.dto.request.TodoRecord;
import java.util.List;

public interface TodoService {

    // Create a new todo
    Object createTodo(TodoRecord todoRecord);

    // Get todo by ID
    Object getTodoById(Long id);

    // Get all todos
    List<Object> getAllTodos();

    // Update an existing todo
    Object updateTodo(Long id, TodoRecord todoRecord);

    // Delete a todo
    void deleteTodo(Long id);

}
