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
 * 1. CLASS DETECTION: Spring detects @RestController during component scan
 * 2. BEAN REGISTRATION: Class is registered as a Spring bean
 * 3. INSTANTIATION: AuthController instance is created and dependencies (@Autowired AuthService) are injected
 * 4. REQUEST MAPPING: @RequestMapping("/api/v1/auth") registers URL base path
 * 5. METHOD MAPPING: Each @PostMapping/@GetMapping registers request handlers
 * 6. READY: Handles incoming HTTP requests and returns JSON responses (@RestController = @Controller + @ResponseBody)
 */
@RestController
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

}
