package com.auth.api.framework.auth.adapter.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignUpRequestValidator(
        @NotBlank(message = "Name is required")
        @Size(
                min = 3,
                max = 100,
                message = "Name must have between 3 and 100 characters"
        )
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,


        @NotBlank(message = "Password is required")
        @Size(
                min = 8,
                message = "Password must have at least 8 characters"
        )
        String password

) {
}
