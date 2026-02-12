package com.managertasks.api.service;

import com.managertasks.api.dto.request.TodoRecord;
import org.springframework.stereotype.Service;
import java.util.*;

@Service("todoServiceInMemory")
public class TodoServiceInMemory implements TodoService {

    // In-memory storage for todos
    private final Map<Long, Map<String, Object>> todoStore = new HashMap<>();
    private Long idCounter = 1L;

    @Override
    public Object createTodo(TodoRecord todoRecord) {
        Long todoId = idCounter++;

        Map<String, Object> todo = new HashMap<>();
        todo.put("id", todoId);
        todo.put("name", todoRecord.name());
        todo.put("priority", todoRecord.priority());
        todo.put("completed", todoRecord.completed() != null ? todoRecord.completed() : false);
        todo.put("userId", todoRecord.userId());
        todo.put("createdAt", new Date());
        todo.put("updatedAt", new Date());

        todoStore.put(todoId, todo);
        return todo;
    }

    @Override
    public Object getTodoById(Long id) {
        if (!todoStore.containsKey(id)) {
            throw new IllegalArgumentException("Todo with ID " + id + " not found");
        }
        return todoStore.get(id);
    }

    @Override
    public List<Object> getAllTodos() {
        return new ArrayList<>(todoStore.values());
    }

    @Override
    public Object updateTodo(Long id, TodoRecord todoRecord) {
        if (!todoStore.containsKey(id)) {
            throw new IllegalArgumentException("Todo with ID " + id + " not found");
        }

        Map<String, Object> todo = todoStore.get(id);
        todo.put("name", todoRecord.name());
        todo.put("priority", todoRecord.priority());
        if (todoRecord.completed() != null) {
            todo.put("completed", todoRecord.completed());
        }
        todo.put("userId", todoRecord.userId());
        todo.put("updatedAt", new Date());

        return todo;
    }

    @Override
    public void deleteTodo(Long id) {
        if (!todoStore.containsKey(id)) {
            throw new IllegalArgumentException("Todo with ID " + id + " not found");
        }
        todoStore.remove(id);
    }

}
