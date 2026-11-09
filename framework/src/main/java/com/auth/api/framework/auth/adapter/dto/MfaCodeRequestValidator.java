package com.auth.api.framework.auth.adapter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record MfaCodeRequestValidator(

        @NotBlank(message = "MFA token is required")
        @Size(max = 128, message = "MFA token is invalid")
        String mfaToken,

        @NotBlank(message = "MFA code is required")
        @Pattern(regexp = "\\d{6}", message = "MFA code must contain 6 digits")
        String code

) {
}
