package com.auth.api.framework.auth.adapter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * @param emailCode obrigatório só quando o sign-in respondeu emailCodeRequired=true
 *                  (código de 6 dígitos enviado por e-mail).
 */
public record MfaSetupRequestValidator(

        @NotBlank(message = "MFA token is required")
        @Size(max = 128, message = "MFA token is invalid")
        String mfaToken,

        @Pattern(regexp = "\\d{6}", message = "Email code must contain 6 digits")
        String emailCode

) {
}
