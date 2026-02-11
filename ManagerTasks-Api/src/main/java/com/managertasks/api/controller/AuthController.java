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

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

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
