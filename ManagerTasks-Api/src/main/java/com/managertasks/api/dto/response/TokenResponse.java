package com.managertasks.api.dto.response;

public record TokenResponse(
    String token,
    String type,
    long expiresIn
) {
}