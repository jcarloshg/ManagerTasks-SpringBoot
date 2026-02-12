package com.managertasks.api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TodoRecord(
        @NotBlank(message = "Name is required") String name,

        @NotBlank(message = "Priority is required") String priority,

        Boolean completed,

        @NotBlank(message = "User ID is required") String userId) {
}
