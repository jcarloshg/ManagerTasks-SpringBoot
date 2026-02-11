package com.managertasks.api.dto.request;

import com.managertasks.api.validation.StrongPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignUpRequest(
    @NotBlank(message = "Name is required")
    String name,

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    String email,

    @NotBlank(message = "Password is required")
    @StrongPassword
    String password
) {
}