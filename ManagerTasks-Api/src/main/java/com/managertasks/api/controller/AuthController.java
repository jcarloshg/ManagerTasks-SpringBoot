package com.managertasks.api.controller;

import com.managertasks.api.dto.request.LoginRequest;
import com.managertasks.api.dto.request.SignUpRequest;
import com.managertasks.api.dto.response.TokenResponse;
import com.managertasks.api.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @RestController Lifecycle:
 *                 1. CLASS DETECTION: Spring detects @RestController during
 *                 component scan
 *                 2. BEAN REGISTRATION: Class is registered as a Spring bean
 *                 3. INSTANTIATION: AuthController instance is created and
 *                 dependencies (@Autowired AuthService) are injected
 *                 4. REQUEST MAPPING: @RequestMapping("/api/v1/auth") registers
 *                 URL base path
 *                 5. METHOD MAPPING: Each @PostMapping/@GetMapping registers
 *                 request handlers
 *                 6. READY: Handles incoming HTTP requests and returns JSON
 *                 responses (@RestController = @Controller + @ResponseBody)
 */
@RestController
// @RequestMapping Lifecycle:
// 1. PARSING: Spring reads @RequestMapping annotation after class detection
// 2. REGISTRATION: Base path "/api/v1/auth" is stored in the request mapping
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

@RequestMapping("/api/v1/auth")
public class AuthController {

    // Injected during step 3 (INSTANTIATION)
    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<TokenResponse> signUp(@Valid @RequestBody SignUpRequest request) {
        TokenResponse response = authService.signUp(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Auth service is healthy");
    }

}
