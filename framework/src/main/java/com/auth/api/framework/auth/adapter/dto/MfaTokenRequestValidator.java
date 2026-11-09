package com.auth.api.framework.auth.adapter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MfaTokenRequestValidator(

        @NotBlank(message = "MFA token is required")
        @Size(max = 128, message = "MFA token is invalid")
        String mfaToken

) {
}
