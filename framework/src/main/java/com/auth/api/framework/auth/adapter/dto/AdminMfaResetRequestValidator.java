package com.auth.api.framework.auth.adapter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * @param reason justificativa registrada na trilha de auditoria
 *               (ex.: "Ticket #4821 - identidade confirmada por videochamada").
 */
public record AdminMfaResetRequestValidator(

        @NotBlank(message = "Reason is required")
        @Size(min = 10, max = 500, message = "Reason must be between 10 and 500 characters")
        String reason

) {
}
