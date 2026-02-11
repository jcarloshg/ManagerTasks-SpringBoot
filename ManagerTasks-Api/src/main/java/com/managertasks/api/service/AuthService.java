package com.managertasks.api.service;

import com.managertasks.api.dto.request.LoginRequest;
import com.managertasks.api.dto.request.SignUpRequest;
import com.managertasks.api.dto.response.TokenResponse;

public interface AuthService {

    TokenResponse signUp(SignUpRequest request);

    TokenResponse login(LoginRequest request);

}