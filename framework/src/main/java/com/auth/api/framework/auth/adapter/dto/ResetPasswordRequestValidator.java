package com.auth.api.framework.auth.adapter.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequestValidator(

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Verification code is required")
        @Size(min = 6, max = 6, message = "Verification code must contain 6 digits")
        String codeOtp,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters")
        String newPassword

) {
}
