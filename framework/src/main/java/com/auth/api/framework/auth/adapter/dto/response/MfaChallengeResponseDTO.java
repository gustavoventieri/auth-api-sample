package com.auth.api.framework.auth.adapter.dto.response;

import com.auth.api.core.auth.application.dto.response.MfaChallengeResponse;
import com.auth.api.core.auth.domain.MfaChallengePurpose;

import java.time.LocalDateTime;

/**
 * @param status            MFA_SETUP_REQUIRED (vincular autenticador) ou MFA_REQUIRED (informar código).
 * @param emailCodeRequired true quando um código foi enviado por e-mail e deve ir no /totp/setup.
 */
public record MfaChallengeResponseDTO(
        String status,
        String mfaToken,
        LocalDateTime expiresAt,
        boolean emailCodeRequired
) {

    public static final String MFA_SETUP_REQUIRED = "MFA_SETUP_REQUIRED";
    public static final String MFA_REQUIRED = "MFA_REQUIRED";

    public static MfaChallengeResponseDTO from(MfaChallengeResponse response) {
        String status = response.purpose() == MfaChallengePurpose.SETUP
                ? MFA_SETUP_REQUIRED
                : MFA_REQUIRED;

        return new MfaChallengeResponseDTO(status, response.mfaToken(), response.expiresAt(), response.emailCodeRequired());
    }
}
