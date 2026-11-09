package com.auth.api.core.auth.application.port.in.mfa;

import com.auth.api.core.auth.application.dto.response.MfaChallengeResponse;
import com.auth.api.core.user.domain.UserDomain;

public interface IssueMfaChallengeUseCase {
    /**
     * @param emailOwnershipProven true quando o usuário acabou de provar a posse do e-mail
     *                             (verify-email). Se false e o MFA estiver PENDING, um código é
     *                             enviado por e-mail e exigido antes do setup do TOTP.
     */
    MfaChallengeResponse execute(UserDomain user, boolean emailOwnershipProven);
}
