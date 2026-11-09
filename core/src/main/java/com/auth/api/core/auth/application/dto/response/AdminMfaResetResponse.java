package com.auth.api.core.auth.application.dto.response;

import com.auth.api.core.shared.enumerated.MfaStatus;

public record AdminMfaResetResponse(
        String userId,
        MfaStatus mfaStatus,
        int revokedSessions
) {
}
