package com.auth.api.framework.auth.adapter.dto.response;

import com.auth.api.core.auth.application.dto.response.AdminMfaResetResponse;

public record AdminMfaResetResponseDTO(
        String userId,
        String mfaStatus,
        int revokedSessions
) {

    public static AdminMfaResetResponseDTO from(AdminMfaResetResponse response) {
        return new AdminMfaResetResponseDTO(
                response.userId(),
                response.mfaStatus().name(),
                response.revokedSessions()
        );
    }
}
