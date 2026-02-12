package com.managertasks.api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // @Autowired Explanation:
    // Instructs Spring to automatically inject a bean of type JwtTokenProvider
    // Spring searches ApplicationContext for a bean matching the type and injects it
    // Occurs during bean instantiation phase (before the filter is used)
    // Eliminates need for manual constructor/setter calls: new JwtTokenProvider() or setJwtTokenProvider()
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String authHeader = request.getHeader(AUTHORIZATION_HEADER);

            if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
                String token = authHeader.substring(BEARER_PREFIX.length());

                if (jwtTokenProvider.validateToken(token)) {
                    String email = jwtTokenProvider.getEmailFromToken(token);

                    UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(email, null, new ArrayList<>());

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception ex) {
            logger.error("Could not validate JWT token", ex);
        }

        filterChain.doFilter(request, response);
    }

}