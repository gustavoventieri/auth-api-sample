package com.auth.api.core.auth.application.dto.response;

import com.auth.api.core.auth.domain.MfaChallengePurpose;

import java.time.LocalDateTime;

public record MfaChallengeResponse(
        MfaChallengePurpose purpose,
        String mfaToken,
        LocalDateTime expiresAt
) {
}
