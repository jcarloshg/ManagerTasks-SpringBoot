package com.managertasks.api.service;

import com.managertasks.api.dto.request.LoginRequest;
import com.managertasks.api.dto.request.SignUpRequest;
import com.managertasks.api.dto.response.TokenResponse;
import com.managertasks.api.entity.User;
import com.managertasks.api.exception.DuplicateEmailException;
import com.managertasks.api.repository.UserRepository;
import com.managertasks.api.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public TokenResponse signUp(SignUpRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException("Email already exists");
        }

        User user = new User(request.name(), request.email(), passwordEncoder.encode(request.password()));
        User savedUser = userRepository.save(user);

        String token = jwtTokenProvider.generateToken(savedUser.getEmail(), savedUser.getId().toString());

        return new TokenResponse(
            token,
            "Bearer",
            jwtTokenProvider.getTokenExpiration()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        String token = jwtTokenProvider.generateToken(user.getEmail(), user.getId().toString());

        return new TokenResponse(
            token,
            "Bearer",
            jwtTokenProvider.getTokenExpiration()
        );
    }

}