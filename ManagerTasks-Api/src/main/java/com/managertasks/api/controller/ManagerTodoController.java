package com.managertasks.api.controller;

import com.managertasks.api.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @RestController Lifecycle:
 *                 1. CLASS DETECTION: Spring detects @RestController during
 *                 component scan
 *                 2. BEAN REGISTRATION: Class is registered as a Spring bean
 *                 3. INSTANTIATION: ManagerTodoController instance is created
 *                 and dependencies are injected
 *                 4. REQUEST MAPPING: @RequestMapping("/api/v1/todo") registers
 *                 URL base path
 *                 5. METHOD MAPPING: Each @PostMapping/@GetMapping registers
 *                 request handlers
 *                 6. READY: Handles incoming HTTP requests and returns JSON
 *                 responses (@RestController = @Controller + @ResponseBody)
 */
@RestController
// @RequestMapping Lifecycle:
// 1. PARSING: Spring reads @RequestMapping annotation after class detection
// 2. REGISTRATION: Base path "/api/v1/todo" is stored in the request mapping
// registry
// 3. METHOD BINDING: Combined with method-level mappings (@PostMapping,
// @GetMapping, etc.)
// 4. ROUTING: Incoming HTTP requests matching this path are routed to this
// controller
// 5. DISPATCH: Spring DispatcherServlet uses this mapping to find the correct
// handler method

// HTTP Method Mapping Annotations Lifecycle (@GetMapping, @PostMapping,
// @PutMapping, @DeleteMapping, @PatchMapping):
// 1. DETECTION: Spring scans methods for HTTP mapping annotations
// 2. PARSING: Extracts HTTP method (GET/POST/PUT/DELETE/PATCH) and URL path
// 3. REGISTRATION: Full endpoint path is registered (base path + method path)
// in DispatcherServlet
// 4. MATCHING: Incoming HTTP requests are matched against registered endpoints
// by method + path
// 5. INVOCATION: Matching request is routed to the corresponding method handler
// 6. RESPONSE: Method executes and returns response (automatically converted to
// JSON by @RestController)

@RequestMapping("/api/v1/todo")
public class ManagerTodoController {

    // @Qualifier Lifecycle:
    // 1. BEAN DETECTION: Spring finds multiple beans implementing TodoService interface
    // 2. AMBIGUITY CHECK: Without @Qualifier, Spring throws NoUniqueBeanDefinitionException
    // 3. QUALIFIER PARSING: Spring reads @Qualifier("beanName") annotation value
    // 4. NAME MATCHING: Spring searches ApplicationContext for bean with matching name
    // 5. BEAN SELECTION: Correct bean instance is selected based on qualifier name
    // 6. INJECTION: Selected bean is injected into the annotated field
    // 7. READY: ManagerTodoController has both services available for use

    @Autowired
    @Qualifier("todoServiceInMemory")
    private TodoService todoServiceInMemory;

    @Autowired
    @Qualifier("todoServicePostgreSQL")
    private TodoService todoServicePostgreSQL;

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Todo service is healthy");
    }

    // @PathVariable Example:
    // Extracts variable from URL path and passes it as method parameter
    // URL pattern: /api/v1/todo/{id} → id value is extracted from the path
    @GetMapping("/{id}")
    public ResponseEntity<String> getTodoById(@PathVariable Long id) {
        return ResponseEntity.ok("Retrieved todo with ID: " + id);
    }

    // @RequestParam Example:
    // Extracts query parameters from URL and passes them as method parameters
    // URL pattern: /api/v1/todo/search?status=completed&priority=high
    // Query parameters are optional by default, use required=true to make mandatory
    @GetMapping("/search/list")
    public ResponseEntity<String> searchTodos(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority) {
        return ResponseEntity.ok("Searching todos with status: " + status + ", priority: " + priority);
    }

    // @Qualifier Example Endpoint 1:
    // Using TodoServiceInMemory (in-memory storage, no database persistence)
    @GetMapping("/all/inmemory")
    public ResponseEntity<Object> getAllTodosFromMemory() {
        return ResponseEntity.ok(todoServiceInMemory.getAllTodos());
    }

    // @Qualifier Example Endpoint 2:
    // Using TodoServicePostgreSQL (database-backed storage)
    @GetMapping("/all/database")
    public ResponseEntity<Object> getAllTodosFromDatabase() {
        return ResponseEntity.ok(todoServicePostgreSQL.getAllTodos());
    }

}
