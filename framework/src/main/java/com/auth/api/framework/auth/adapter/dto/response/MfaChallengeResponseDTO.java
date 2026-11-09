package com.auth.api.framework.auth.adapter.dto.response;

import com.auth.api.core.auth.application.dto.response.MfaChallengeResponse;
import com.auth.api.core.auth.domain.MfaChallengePurpose;

import java.time.LocalDateTime;

/**
 * @param status MFA_SETUP_REQUIRED (vincular autenticador) ou MFA_REQUIRED (informar código).
 */
public record MfaChallengeResponseDTO(
        String status,
        String mfaToken,
        LocalDateTime expiresAt
) {

    public static final String MFA_SETUP_REQUIRED = "MFA_SETUP_REQUIRED";
    public static final String MFA_REQUIRED = "MFA_REQUIRED";

    public static MfaChallengeResponseDTO from(MfaChallengeResponse response) {
        String status = response.purpose() == MfaChallengePurpose.SETUP
                ? MFA_SETUP_REQUIRED
                : MFA_REQUIRED;

        return new MfaChallengeResponseDTO(status, response.mfaToken(), response.expiresAt());
    }
}
