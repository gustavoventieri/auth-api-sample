package com.auth.api.framework.auth.adapter.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequestValidator(

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email

) {
}
