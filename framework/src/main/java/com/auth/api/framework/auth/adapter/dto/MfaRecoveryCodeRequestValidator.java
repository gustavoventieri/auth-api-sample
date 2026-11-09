package com.auth.api.framework.auth.adapter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MfaRecoveryCodeRequestValidator(

        @NotBlank(message = "MFA token is required")
        @Size(max = 128, message = "MFA token is invalid")
        String mfaToken,

        @NotBlank(message = "Recovery code is required")
        @Size(max = 32, message = "Recovery code is invalid")
        String recoveryCode

) {
}
