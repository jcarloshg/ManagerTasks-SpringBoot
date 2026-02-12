package com.managertasks.api.config;

import com.managertasks.api.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @Configuration Annotation Lifecycle:
 *
 * 1. CLASS DETECTION (Startup Phase):
 *    - Spring scans the classpath during application startup
 *    - Finds classes annotated with @Configuration
 *    - Recognizes them as bean definition sources
 *
 * 2. BEAN DEFINITION REGISTRATION (Initialization Phase):
 *    - The @Configuration class itself is registered as a bean
 *    - Spring parses all methods annotated with @Bean
 *    - Creates bean definitions for each @Bean method
 *    - Stores these definitions in the BeanFactory
 *
 * 3. BEAN INSTANTIATION (Creation Phase):
 *    - Spring instantiates the SecurityConfig class
 *    - Constructor is invoked (or default constructor if none exists)
 *    - Dependency injection (@Autowired) occurs for class fields
 *
 * 4. @BEAN METHOD EXECUTION (Bean Creation Phase):
 *    - Each @Bean method is called to create actual bean instances
 *    - Methods are executed in dependency order
 *    - Order: passwordEncoder() → authenticationManager() → securityFilterChain()
 *    - Returned objects are registered in the ApplicationContext
 *
 * 5. DEPENDENCY INJECTION (Wiring Phase):
 *    - Created beans are injected into other beans that depend on them
 *    - SecurityFilterChain depends on HttpSecurity parameter (injected by Spring)
 *    - AuthenticationManager depends on AuthenticationConfiguration (injected by Spring)
 *
 * 6. APPLICATION READY (Ready Phase):
 *    - All beans are fully initialized and wired
 *    - Application context is ready for use
 *    - Beans can now be used throughout the application lifecycle
 */
@Configuration
public class SecurityConfig {

    // Dependency injected during SecurityConfig bean instantiation (Phase 3)
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    // Phase 4: First @Bean method called
    // Creates and registers PasswordEncoder bean in ApplicationContext
    // No dependencies required, can be executed first
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Phase 4: Second @Bean method called
    // Creates AuthenticationManager bean
    // AuthenticationConfiguration is auto-injected by Spring (Phase 5 dependency wiring)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // Phase 4: Last @Bean method called (depends on jwtAuthenticationFilter from Phase 3)
    // Creates SecurityFilterChain bean that configures Spring Security
    // HttpSecurity is auto-injected by Spring, jwtAuthenticationFilter was already injected into the class
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}